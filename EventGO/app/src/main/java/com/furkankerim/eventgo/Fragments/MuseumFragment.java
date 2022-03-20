package com.furkankerim.eventgo.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.CategoryAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.Map;

public class MuseumFragment extends Fragment {
    private View v;
    private RecyclerView mRecycler;
    private CategoryAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private ArrayList<CategoryItem> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_museum, container, false);

        categoryList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();

        mRecycler = v.findViewById(R.id.recyclerviewMuseum);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        getDataFirestore();

        mAdapter = new CategoryAdapter(categoryList, v.getContext());
        mRecycler.setAdapter(mAdapter);

        return v;
    }

    private void getDataFirestore() {
        mFirestore.collection("Posts").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null) {
                    Toast.makeText(v.getContext(), error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }else {
                    categoryList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String category = (String) data.get("category");
                        if (category.matches("Museum")) {
                            String eventname = (String) data.get("eventname");
                            String downloadURL = (String) data.get("url");
                            String location = (String) data.get("location");
                            String date = (String) data.get("date");
                            String hour = (String) data.get("hour");
                            String info = (String) data.get("info");
                            String uuid = (String) data.get("postID");
                            String latitude = (String) data.get("latitude");
                            String longitude = (String) data.get("longitude");
                            String price = (String) data.get("price");
                            String count = (String) data.get("count");
                            String organizerID = (String) data.get("organizerID");
                            String starCount = (String) data.get("starCount");
                            CategoryItem categoryItem = new CategoryItem(eventname, location, date, downloadURL, info, hour,uuid,latitude,longitude,price,count,organizerID,starCount);
                            categoryList.add(categoryItem);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}