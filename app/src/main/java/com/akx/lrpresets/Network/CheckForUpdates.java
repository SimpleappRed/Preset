package com.akx.lrpresets.Network;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.akx.lrpresets.R;

public class CheckForUpdates extends AsyncTask {

    FirebaseFirestore firebaseFirestore;
    Activity activity;

    String versionName;
    boolean isCancelable,isShowable;

    public CheckForUpdates(Activity activity){
        this.activity=activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Log.d("TAGER", "checking for updates");

        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Utils").document("update").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    versionName = documentSnapshot.getString("versionName");
                    isCancelable=documentSnapshot.getBoolean("isCancelable");
                    isShowable=documentSnapshot.getBoolean("isShowable");

                    if(!versionName.equals(com.akx.lrpresets.BuildConfig.VERSION_NAME)){
                        if(isShowable){
                            showUpdationDialog(activity,isCancelable);
                        }
                    }

                }
            }
        });
        return null;
    }

    private void showUpdationDialog(final Activity activity,boolean isCancelable) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialog.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialog.setContentView(R.layout.dialog_layout);
        TextView txtDialogTitle, txtDialogDesc;
        Button btnDialogPostive, btnDialogNegative;

        txtDialogTitle = dialog.findViewById(R.id.txtDialogTitle);
        txtDialogDesc = dialog.findViewById(R.id.txtDialogDesc);
        btnDialogPostive = dialog.findViewById(R.id.btnDialogPositive);
        btnDialogNegative = dialog.findViewById(R.id.btnDialogNegative);

        if(isCancelable) {
            dialog.setCancelable(true);
            txtDialogTitle.setText("Update available");
            txtDialogDesc.setText("New version of app is available. Would you like to install new version of the app ?");
            btnDialogPostive.setText("Update");
            btnDialogNegative.setText("Later");
        }else {
            dialog.setCancelable(false);
            txtDialogTitle.setText("Update required");
            txtDialogDesc.setText("New version of app is available. Update the app to the latest version to continue.");
            btnDialogPostive.setText("Update");
            btnDialogNegative.setVisibility(View.INVISIBLE);
        }

        dialog.show();

        btnDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();dialog.dismiss();
            }
        });

        btnDialogPostive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.lrmobile")));
            }
        });
    }
}
