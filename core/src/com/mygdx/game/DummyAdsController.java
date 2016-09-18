package com.mygdx.game;

/**
 * Created by Isaac Assegai on 9/17/16.
 */
public class DummyAdsController implements AdsController {
    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showBannerAd() {
        System.out.println("showBannerAd Dummy");
    }

    @Override
    public void hideBannerAd() {
        System.out.println("hideBannerAd Dummy");
    }

    @Override
    public void showInterstitialAd(Runnable then) {

    }
}
