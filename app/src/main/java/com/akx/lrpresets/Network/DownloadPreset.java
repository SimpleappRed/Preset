package com.akx.lrpresets.Network;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akx.lrpresets.Model.Preset;
import com.akx.lrpresets.R;
import com.bumptech.glide.util.Util;
import com.gun0912.tedpermission.PermissionListener;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class DownloadPreset extends AsyncTask {

    Activity activity;
    RelativeLayout layoutAdd;
    Preset preset;

    private ProgressBar progressBar;
    private ImageView imgAdd;
    private TextView txtAdd;

    public DownloadPreset(Activity activity, RelativeLayout layoutAdd, Preset preset) {
        this.activity = activity;
        this.layoutAdd = layoutAdd;
        this.preset = preset;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Log.d("TAGER", "doInBackground() is calling");

        Utils.downloadList.add(preset.getName());


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!preset.isLocked) {
                    ImageView imgDownload = layoutAdd.findViewById(R.id.imgDownload);
                    imgDownload.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_download));
                }


                progressBar = layoutAdd.findViewById(R.id.progressBar);
                imgAdd = layoutAdd.findViewById(R.id.imgDownload);
                txtAdd = layoutAdd.findViewById(R.id.txtOpen);

                imgAdd.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                txtAdd.setText("Downloading");
            }
        });


        URL url = null;
        try {
            url = new URL(preset.getDng());
            InputStream inputStream = url.openStream();

            DataInputStream dataInputStream = new DataInputStream(inputStream);

            byte[] buffer = new byte[1024];
            int length;

            final File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.akx.lrpresets");

            if (!file.exists()) {
                boolean isCreated = file.mkdirs();
                if (isCreated) {
                    Log.d("TAGER", "created successfully");
                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() +
                    "/Android/data/com.akx.lrpresets/" + preset.getName() + ".dng"));

            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
                Log.d("TAGER", "FIle downloaind");
            }


            Log.d("TAGER", "file successfully downloaded");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        Log.d("TAGER", "post execute is calling");


        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.akx.lrpresets/" + preset.getName() + ".dng");
        Uri uri = FileProvider.getUriForFile(activity, "com.akx.lrpresets.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setPackage("com.adobe.lrmobile");
        intent.setType("image/dng");

        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        sendNotification(preset.getName());
        Utils.downloadList.remove(preset.getName());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgAdd.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_done_black_24dp));
                imgAdd.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                txtAdd.setText("Done");
                Toast.makeText(activity, preset.getName() + " is added", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("TAGER", "file is shared tp lightroom");
    }

    public void sendNotification(String title) {
        int notificationId = Utils.notificationId++;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title + " is added successfully")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSound(path);
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chennalId = "YOUR_CHENNAL_ID";
            NotificationChannel channel = new NotificationChannel(chennalId, "myChannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(chennalId);
        }
        notificationManager.notify(notificationId, builder.build());
    }
}
