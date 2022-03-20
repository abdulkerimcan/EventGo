package com.furkankerim.eventgo.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.CategoryAdapter;
import com.furkankerim.eventgo.Adapters.FavoritesAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FavoritesFragment extends Fragment {
    private View v;
    private FavoritesAdapter mAdapter;
    private RecyclerView mRecycler;

    private FirebaseFirestore mFirestore;

    private ArrayList<CategoryItem> categoryList;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_favorites, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        categoryList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();

        mRecycler = v.findViewById(R.id.recyclerviewFavorite);
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecycler.setLayoutManager(layoutManager);




        if (mUser != null) {
            getDataFirestore();
            mAdapter = new FavoritesAdapter(categoryList, v.getContext());


            mRecycler.setAdapter(mAdapter);
        }
        return v;
    }

    private void getDataFirestore() {
        mFirestore.collection("Favorites").document(mUser.getUid()).collection("FavoriteItems").orderBy("addingTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(v.getContext(), error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    categoryList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {


                        Map<String, Object> data = snapshot.getData();
                        if (data != null) {
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
                            CategoryItem categoryItem = new CategoryItem(eventname, location, date, downloadURL, info, hour, uuid,latitude,longitude,price,count,organizerID,starCount);
                            categoryList.add(categoryItem);
                            mAdapter.notifyDataSetChanged();


                        }
                    }
                }
            }
        });
    }
}