package com.akx.lrpresets.Fragment;

import android.app.Activity;
import android.os.Bundle;
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
import com.akx.lrpresets.Adapter.CollectionAdapter;
import com.akx.lrpresets.Model.Collection;
import com.akx.lrpresets.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class FragmentCollections extends Fragment {

    RecyclerView recyclerView;
    RelativeLayout progressLayout;
    View view;

    List<Collection> collectionList;
    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_collections, null);

        initializeViews();


        getCollections();


        return view;
    }

    private void initializeViews() {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressLayout = view.findViewById(R.id.progressLayout);

        collectionList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void getCollections() {
        try {
            firebaseFirestore.collection("Collection").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    try {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : documentSnapshots) {
                                Collection collection = snapshot.toObject(Collection.class);
                                collectionList.add(collection);
                            }
                            initializeRecyclerView(collectionList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    TextView txtStatus = progressLayout.findViewById(R.id.txtStatus);
                    txtStatus.setText("Something went wrong");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeRecyclerView(List<Collection> collectionList) {

        progressLayout.setVisibility(View.INVISIBLE);

        try {
            CollectionAdapter collectionAdapter = new CollectionAdapter((Activity) getContext(), collectionList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setAdapter(collectionAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }



    }
}
