package org.cocos2dx.cpp;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nguyenhoang.game.GoogleLeaderboards;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class AppActivity extends  Cocos2dxActivity
{
	// new activity
	private static AppActivity _appActiviy;
	private static AdView adView;
	private static InterstitialAd mInterstitialAd; 
	private static boolean mInterstitialAdError = false;
	private static final String ADbaner_UNIT_ID = "ca-app-pub-6021678406318751/1304132225";
	private static final String AD_FULL_ID = "ca-app-pub-6021678406318751/2780865429";

    public static final String TAG = "SavedGames";
    private static final int APP_STATE_KEY = 0;
    // Request code used to invoke sign-in UI.
    private static final int RC_SIGN_IN = 9001;
    // Request code used to invoke Snapshot selection UI.
    private static final int RC_SELECT_SNAPSHOT = 9002;
    private boolean mIsResolving = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignIn = true;
      private static boolean statusAdmod = false;
      
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private Point getDisplaySizeGE11(Display d)
    {
        Point p = new Point(0, 0);
        d.getSize(p);
        return p;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterstitialAdError = false;
        
        // new
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                                                                                           AdView.LayoutParams.WRAP_CONTENT,
                                                                                           AdView.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        
        // banner admod android
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(ADbaner_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("HASH_DEVICE_ID")
        .build();
        adView.loadAd(adRequest);
        adView.setBackgroundColor(Color.BLACK);
        adView.setBackgroundColor(0);
        RelativeLayout adLayer = new RelativeLayout(this);
        adLayer.addView(adView, relativeLayoutParams);
        ((FrameLayout)Cocos2dxGLSurfaceView.getInstance().getParent()).addView(adLayer);
        

        /* leaderboard */
        GoogleLeaderboards.getInstance().init(this);
        
        
        // InterstitialAD
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_FULL_ID);
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                AdRequest newadRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("HASH_DEVICE_ID")
                .build();
                mInterstitialAd.loadAd(newadRequest);
                adView.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onAdFailedToLoad(int errorCode) {
                mInterstitialAdError = true;
            }
        });
        
        _appActiviy = this;
        
    }
    
    
    public static void hideAd(){
        _appActiviy.runOnUiThread(new Runnable()
                                  {
            @Override
            public void run()
            {
                if (_appActiviy.adView.isEnabled())
                    _appActiviy.adView.setEnabled(false);
                if (_appActiviy.adView.getVisibility() != 4 )
                    _appActiviy.adView.setVisibility(View.INVISIBLE);
                Log.d("", "hide  banner");
            }
        });
    }
    
    
    public static void showAd(){
        _appActiviy.runOnUiThread(new Runnable()
                                  {
            @Override
            public void run()
            {
                if (!_appActiviy.adView.isEnabled())
                    _appActiviy.adView.setEnabled(true);
                if (_appActiviy.adView.getVisibility() == 4 )
                    _appActiviy.adView.setVisibility(View.VISIBLE);
                Log.d("", "show  banner");
            }
        });
    }
    
    
    public static void showInterstitialAd(){
        _appActiviy.runOnUiThread(new Runnable()
                                  {
            @Override
            public void run()
            {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("", "show full banner");
                    statusAdmod = true;
                }
                else if(mInterstitialAdError){
                    statusAdmod = false;
                    mInterstitialAdError = false;
                    AdRequest newadRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("HASH_DEVICE_ID")
                    .build();
                    mInterstitialAd.loadAd(newadRequest);
                    adView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public static boolean getStatusAdMode(){
        return statusAdmod;
    }
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleLeaderboards.getInstance().onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onStart(){
        super.onStart();
        GoogleLeaderboards.getInstance().onStart();
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        GoogleLeaderboards.getInstance().onStop();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
    
    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }
}
