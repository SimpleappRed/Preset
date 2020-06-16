package com.akx.lrpresets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.akx.lrpresets.Adapter.PresetAdapter;
import com.akx.lrpresets.Model.Collection;
import com.akx.lrpresets.Model.Preset;

import java.util.ArrayList;
import java.util.List;

public class PresetActivity extends AppCompatActivity {

    public static Collection COLLECTION;
    FirebaseFirestore firebaseFirestore;
    List<Preset> presetList;


    RecyclerView recyclerView;
    RelativeLayout progressLayout;
    TextView txtTitle,txtSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        initializeViews();

        loadPresets();

    }

    private void initializeViews(){
        recyclerView=findViewById(R.id.recyclerView);
        txtTitle=findViewById(R.id.txtTitle);
        txtSize=findViewById(R.id.txtSize);
        progressLayout=findViewById(R.id.progressLayout);


        firebaseFirestore=FirebaseFirestore.getInstance();
        presetList=new ArrayList<>();

        if(COLLECTION!=null){
            txtTitle.setText(COLLECTION.getName());
            txtSize.setText(COLLECTION.getSize()+" presets available");
        }
        else {
            onBackPressed();
        }


        findViewById(R.id.layoutHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        MobileAds.initialize(this,getString(R.string.app_id));

    }

    private void loadPresets(){
        firebaseFirestore.collection("Collection")
                .document(COLLECTION.getId())
                .collection("Preset")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try{
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot:snapshotList){
                            presetList.add(snapshot.toObject(Preset.class));
                        }
                        initializeRecylerView();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TextView txtStatus=progressLayout.findViewById(R.id.progressLayout);
                txtStatus.setText("Something went wrong");
            }
        });
    }

    private void initializeRecylerView(){
        Log.d("TAGER", "initializing recyclerView");

        progressLayout.setVisibility(View.INVISIBLE);

        try {
            PresetAdapter presetAdapter=new PresetAdapter(PresetActivity.this, presetList);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setHasFixedSize(true);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setAdapter(presetAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(PresetActivity.this));
//        SnapHelper snapHelper=new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }



    }



}
