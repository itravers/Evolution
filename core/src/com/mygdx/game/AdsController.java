package com.mygdx.game;

/**
 * Created by Isaac Assegai on 9/17/16.
 */
public interface AdsController {
    public boolean isWifiConnected();
    public void showBannerAd();
    public void hideBannerAd();
    public void showInterstitialAd (Runnable then);
   // public void hideInterstitialAd (Runnable then);
}
