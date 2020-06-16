package com.akx.lrpresets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.android.gms.ads.MobileAds;
import com.akx.lrpresets.Adapter.PagerViewAdapter;
import com.akx.lrpresets.Network.CheckForUpdates;
import com.akx.lrpresets.Network.Utils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerViewAdapter pagerViewAdapter;
    RelativeLayout layoutCollection,layoutTrend;
    TextView txtTrend,txtCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        initializeViewPager();

        Utils.initializeInterstitialAd(MainActivity.this);


        Utils.checkPermissions(MainActivity.this, new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        });

        try {
            new CheckForUpdates(MainActivity.this).execute();
        }catch (Exception e){e.printStackTrace();}



//        File file=new File(Environment.getExternalStorageDirectory()+"/Android/data/com.akx.lrpresets/Preset 1.dng");


//        File file=new File(Environment.getExternalStorageDirectory()+"/Preset 1.dng");
//        Uri uri = Uri.fromFile(file);
//        Uri rt= FileProvider.getUriForFile(MainActivity.this,"com.akx.lrpresets.fileprovider",file);
//        Toast.makeText(this, rt.toString(), Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_STREAM,rt);
//        intent.putExtra(Intent.EXTRA_TITLE,"rewr");
//        intent.setPackage("com.adobe.lrmobile");
//        intent.setType("image/dng");
//        startActivity(intent);

//        PackageManager manager=getPackageManager();
//
//        List<ApplicationInfo> infoList=manager.getInstalledApplications(PackageManager.GET_META_DATA);



    }

    private void initializeViews(){
        viewPager=findViewById(R.id.viewPager);
        layoutCollection=findViewById(R.id.layoutCollection);
        layoutTrend=findViewById(R.id.layoutTrend);
        txtTrend=findViewById(R.id.txtTrend);
        txtCollection=findViewById(R.id.txtCollection);

        MobileAds.initialize(this, getString(R.string.app_id));
    }

    private void initializeViewPager(){
        pagerViewAdapter=new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    layoutCollection.setAlpha((float) 0.6);
                    layoutTrend.setAlpha((float)1.0);

                }
                else {
                    layoutCollection.setAlpha((float) 1.0);
                    layoutTrend.setAlpha((float) 0.6);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layoutCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { viewPager.setCurrentItem(1);
            }
        });

        layoutTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
    }


}
