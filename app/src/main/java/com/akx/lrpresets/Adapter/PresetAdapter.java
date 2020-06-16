package com.akx.lrpresets.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.akx.lrpresets.Model.Preset;
import com.akx.lrpresets.Network.DownloadPreset;
import com.akx.lrpresets.Network.Utils;
import com.akx.lrpresets.R;
import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    Activity activity;
    List<Preset> presetList;

    public PresetAdapter(Activity activity, List<Preset> presetList) {
        this.activity = activity;
        this.presetList = presetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtname.setText(presetList.get(position).getName());
        holder.txtDesc.setText(presetList.get(position).getDesc());

//        Animation animation= AnimationUtils.loadAnimation(activity, R.anim.list_anim);
//        holder.parentCard.startAnimation(animation);

        if (presetList.get(position).isLocked && !Utils.unlockedPresetList.contains(presetList.get(position).getName())) {
            holder.imgDownload.setPadding(4, 4, 4, 6);
            holder.imgDownload.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_lock));
        }

        Glide.with(activity)
                .load(presetList.get(position).getImgAfter())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressLayout.setVisibility(View.INVISIBLE);

                        holder.parentCard.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        holder.parentCard.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        return false;
                    }
                }).into(holder.imgAfter);

        Glide.with(activity)
                .load(presetList.get(position).getImgBefore())
                .into(holder.imgBefore);

        holder.imgOriginal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.imgAfter.setVisibility(View.INVISIBLE);
                    holder.imgBefore.setVisibility(View.VISIBLE);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    holder.imgBefore.setVisibility(View.INVISIBLE);
                    holder.imgAfter.setVisibility(View.VISIBLE);
                    return true;
                } else
                    return false;
            }
        });


        holder.layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Utils.isInternetAvailable(activity)) {
                    Toast.makeText(activity, "Internet Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Utils.showInterstitialAd(activity);

                if (!Utils.isLrInstalled(activity)) {
                    showInstallationDialog(activity);
                    return;
                }

                Utils.checkPermissions(activity, new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.akx.lrpresets");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (presetList.get(position).isLocked && !Utils.unlockedPresetList.contains(presetList.get(position).getName())) {
                            showRewardDialog(activity, holder.layoutAdd, presetList.get(position));
                            return;
                        }

                        if (Utils.downloadList.contains(presetList.get(position).getName())) {
                            Toast.makeText(activity, "Already in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            new DownloadPreset(activity, holder.layoutAdd, presetList.get(position)).execute();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return presetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAfter, imgBefore, imgOriginal, imgDownload;
        CardView parentCard;
        TextView txtname, txtDesc;
        RelativeLayout layoutAdd, progressLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAfter = itemView.findViewById(R.id.imgAfter);
            imgDownload = itemView.findViewById(R.id.imgDownload);
            imgBefore = itemView.findViewById(R.id.imgBefore);
            txtname = itemView.findViewById(R.id.txtName);
            progressLayout = itemView.findViewById(R.id.progressLayout);
            layoutAdd = itemView.findViewById(R.id.layoutAdd);
            parentCard = itemView.findViewById(R.id.parentCard);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            imgOriginal = itemView.findViewById(R.id.imgOriginal);
        }
    }


    private void showInstallationDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialog.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(true);

        TextView txtDialogTitle, txtDialogDesc;
        Button btnDialogPostive, btnDialogNegative;

        txtDialogTitle = dialog.findViewById(R.id.txtDialogTitle);
        txtDialogDesc = dialog.findViewById(R.id.txtDialogDesc);
        btnDialogPostive = dialog.findViewById(R.id.btnDialogPositive);
        btnDialogNegative = dialog.findViewById(R.id.btnDialogNegative);

        txtDialogTitle.setText("Lightroom not installed");
        txtDialogDesc.setText("It seems like you have not installed Adobe Lightroom. Install Adobe lightroom to continue.");
        btnDialogNegative.setVisibility(View.INVISIBLE);
        btnDialogPostive.setText("Install");

        dialog.show();

        btnDialogPostive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.lrmobile")));
            }
        });
    }

    private void showRewardDialog(final Activity activity, final RelativeLayout layoutAdd, final Preset preset) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialog.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(true);

        TextView txtDialogTitle, txtDialogDesc;
        Button btnDialogPostive, btnDialogNegative;

        txtDialogTitle = dialog.findViewById(R.id.txtDialogTitle);
        txtDialogDesc = dialog.findViewById(R.id.txtDialogDesc);
        btnDialogPostive = dialog.findViewById(R.id.btnDialogPositive);
        btnDialogNegative = dialog.findViewById(R.id.btnDialogNegative);

        txtDialogTitle.setText("Unlock akx");
        txtDialogDesc.setText("Watch a video ad to unlock the akx");
        btnDialogNegative.setText("Cancel");
        btnDialogPostive.setText("Watch Ad");

        dialog.show();

        btnDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        btnDialogPostive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show rewarded ad
                Log.d("TAGER", "watch button click");
                if (Utils.isInternetAvailable(activity)) {
                    dialog.cancel();
                    dialog.dismiss();
                    Utils.initializeRewardedAd(activity, layoutAdd, preset);
                } else {
                    Toast.makeText(activity, "Internet required", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void checkForFolder() {

    }
}
