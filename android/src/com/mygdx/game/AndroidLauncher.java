package com.mygdx.game;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mygdx.game.MyGdxGame;

public class AndroidLauncher extends AndroidApplication implements AdsController{


	private static final String BANNER1_AD_UNIT_ID = "ca-app-pub-5553172650479270/8394537144";
	private static final String INTERSTITIAL1_AD_UNIT_ID = "ca-app-pub-5553172650479270/7755408742";
	AdView bannerAd;
	InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*RelativeLayout layout = new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		//Create the libgdx view.
		View gameView = initializeForView(new MyGdxGame(), config);

		// Create and setup the AdMob view
		AdView adView = new AdView(this); // Put in your secret key here
		adView.loadAd(new AdRequest());


		//initialize(new MyGdxGame(), config);*/


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Create a gameView and a bannerAd AdView
		View gameView = initializeForView(new MyGdxGame(this), config);
		setupAds();

		// Define the layout
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView(bannerAd, params);

		setContentView(layout);
	}

	public void setupAds() {
		bannerAd = new AdView(this);
		bannerAd.setVisibility(View.INVISIBLE);
		//bannerAd.setBackgroundColor(0x000000ff); // black
		bannerAd.setAdUnitId(BANNER1_AD_UNIT_ID);
		bannerAd.setAdSize(AdSize.SMART_BANNER);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL1_AD_UNIT_ID);

		AdRequest.Builder builder = new AdRequest.Builder();
		AdRequest ad = builder.build();
		interstitialAd.loadAd(ad);
	}

	@Override
	public boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return (ni != null && ni.isConnected());
	}

	@Override
	public void showBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				AdRequest.Builder builder = new AdRequest.Builder();
				builder.addTestDevice("752B44EB5165C7A81E9423963C07AC77");


				AdRequest ad = builder.build();
				System.out.println("ads Pre loadAd");
				bannerAd.loadAd(ad);

				bannerAd.setAdListener(new AdListener() {
					@Override
					public void onAdLoaded() {
						super.onAdLoaded();
						System.out.println("ads Post loadAd");
						bannerAd.setBackgroundColor(0xff000000);
						bannerAd.setVisibility(View.VISIBLE);
					}
				});

			}
		});
	}

	@Override
	public void hideBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void showInterstitialAd(final Runnable then) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (then != null) {
					interstitialAd.setAdListener(new AdListener() {
						@Override
						public void onAdClosed() {
							//Gdx.app.postRunnable(then);
							AdRequest.Builder builder = new AdRequest.Builder();
							builder.addTestDevice("752B44EB5165C7A81E9423963C07AC77");
							AdRequest ad = builder.build();
							interstitialAd.loadAd(ad);
						}
					});
				}
				interstitialAd.show();
			}
		});
	}
}
