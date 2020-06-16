package com.akx.lrpresets.Network;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.akx.lrpresets.Model.Preset;
import com.akx.lrpresets.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    //contains download queue
    public static List<String> downloadList = new ArrayList<>();

    //change for each notification
    public static int notificationId = 1;

    //contains unlocked presets by reward
    public static List<String> unlockedPresetList = new ArrayList<>();

    //checks internet available
    public static boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public static InterstitialAd interstitialAd;
    public static int counter = 0;

    public static void initializeInterstitialAd(Activity activity) {
        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(activity.getString(R.string.interstitial_ad));
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(activity.getString(R.string.test_device_id)).build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("TAGER", "interstitial ad is loaded");
            }
        });
    }

    public static void showInterstitialAd(final Activity activity) {
        if (interstitialAd.isLoaded()) {
            if (counter == 0) {
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        counter++;
                        Log.d("TAGER", "counter ++ ");
                        initializeInterstitialAd(activity);
                    }
                });
                interstitialAd.show();
            } else {
                Log.d("TAGER", "counter = 0");
                counter = 0;
            }
        }
    }


    public static RewardedVideoAd rewardedVideoAd;
    public static boolean isLoading = false;

    public static void initializeRewardedAd(final Activity activity, final RelativeLayout layoutAdd, final Preset preset) {

        Log.d("TAGER", "initialize Reaward is calling");
        progressDialog = getProgressDialog(activity);
        progressDialog.show();


        if (rewardedVideoAd != null) {
            if (rewardedVideoAd.isLoaded() && progressDialog.isShowing()) {
                rewardedVideoAd.show();
                Log.d("TAGER", "ad alredy loaded");
                return;
            }
        }

        if (!isLoading) {
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
            rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    isLoading = false;
                    if (progressDialog.isShowing()) {
                        rewardedVideoAd.show();
                    }
                    Log.d("TAGER", "RewardedAd loaded");
                }

                @Override
                public void onRewardedVideoAdOpened() {
                    progressDialog.dismiss();progressDialog.cancel();
                }

                @Override
                public void onRewardedVideoStarted() {

                }

                @Override
                public void onRewardedVideoAdClosed() {
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                    Log.d("TAGER", "User rewarded successfully");
                    Toast.makeText(activity, "Preset downloading...", Toast.LENGTH_SHORT).show();
                    Utils.unlockedPresetList.add(preset.getName());
                    new DownloadPreset(activity, layoutAdd, preset).execute();
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    isLoading=false;
                    rewardedVideoAd=null;
                    if(progressDialog.isShowing()){
                        Toast.makeText(activity, "Try again later", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();progressDialog.dismiss();
                    }
                }

                @Override
                public void onRewardedVideoCompleted() {

                }
            });
            rewardedVideoAd.loadAd(activity.getString(R.string.rewarded_ad),
                    new AdRequest.Builder().addTestDevice(activity.getString(R.string.test_device_id)).build());
            isLoading = true;
        }

    }


    public static boolean isLrInstalled(Activity activity) {
        PackageManager manager = activity.getPackageManager();
        List<ApplicationInfo> infoList = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : infoList) {
            if (info.packageName.equals("com.adobe.lrmobile")) {
                return true;
            }
        }
        return false;
    }


    static ProgressDialog progressDialog = null;

    public static ProgressDialog getProgressDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Ad...");
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public static void checkPermissions(Activity activity,PermissionListener permissionListener){
        TedPermission.with(activity)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("It is required to download presets")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
