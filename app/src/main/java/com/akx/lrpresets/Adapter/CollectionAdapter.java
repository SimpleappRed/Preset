package com.akx.lrpresets.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.akx.lrpresets.Model.Collection;
import com.akx.lrpresets.PresetActivity;
import com.akx.lrpresets.R;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    Activity activity;
    List<Collection> collectionList;

    public CollectionAdapter(Activity activity, List<Collection> collectionList) {
        this.activity = activity;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtName.setText(collectionList.get(position).getName());
        holder.txtSize.setText(collectionList.get(position).getSize()+" presets available");

        Glide.with(activity).load(collectionList.get(position).getImgUrl()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                // show herer somw issue
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                holder.progressLayout.setVisibility(View.INVISIBLE);

                holder.cardParent.getLayoutParams().height= RelativeLayout.LayoutParams.WRAP_CONTENT;
                holder.cardParent.getLayoutParams().width=RelativeLayout.LayoutParams.WRAP_CONTENT;
                return false;
            }
        }).into(holder.imgCollection);

        holder.cardParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, PresetActivity.class);
                PresetActivity.COLLECTION=collectionList.get(position);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtSize;
        ImageView imgCollection;
        CardView cardParent;
        RelativeLayout progressLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            txtSize=itemView.findViewById(R.id.txtSize);
            imgCollection=itemView.findViewById(R.id.imgCollection);
            cardParent=itemView.findViewById(R.id.parentCard);
            progressLayout=itemView.findViewById(R.id.progressLayout);
        }
    }
}
