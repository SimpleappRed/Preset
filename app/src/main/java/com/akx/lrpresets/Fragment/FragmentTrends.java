package com.akx.lrpresets.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.akx.lrpresets.Adapter.PresetAdapter;
import com.akx.lrpresets.Model.Preset;
import com.akx.lrpresets.R;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentTrends extends Fragment {

    FirebaseFirestore firebaseFirestore;
    List<Preset> presetList;
    View view;


    RecyclerView recyclerView;
    RelativeLayout progressLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_trends,null);


        initializeViews();

        loadPresets();

        return view;
    }

    private void initializeViews(){
        recyclerView=view.findViewById(R.id.recyclerView);
        progressLayout=view.findViewById(R.id.progressLayout);

        firebaseFirestore=FirebaseFirestore.getInstance();
        presetList=new ArrayList<>();
    }

    private void loadPresets(){
        firebaseFirestore.collection("Top Trends")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
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

                TextView txtStatus=progressLayout.findViewById(R.id.txtStatus);
                txtStatus.setText("Something went wrong");
            }
        });
    }

    private void initializeRecylerView(){
        Log.d("TAGER", "initializing recyclerView");

        progressLayout.setVisibility(View.INVISIBLE);

        try {
            PresetAdapter presetAdapter=new PresetAdapter((Activity)getContext(), presetList);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setAdapter(presetAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager((Activity)getContext()));
//        SnapHelper snapHelper=new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }

}
