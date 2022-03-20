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
import com.furkankerim.eventgo.Adapters.TicketAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class MyEventsFragment extends Fragment {

    private View v;
    private RecyclerView mRecycler;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CategoryAdapter mAdapter;
    private ArrayList<CategoryItem> itemArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v =  inflater.inflate(R.layout.fragment_my_events, container, false);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRecycler = v.findViewById(R.id.recyclervievMyEvents);
        itemArrayList = new ArrayList<>();
        mAdapter = new CategoryAdapter(itemArrayList,getContext());

        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecycler.setAdapter(mAdapter);


        mFirestore.collection("Posts").whereEqualTo("organizerID",mUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }else {
                            itemArrayList.clear();
                            for (DocumentSnapshot ds : value.getDocuments()) {
                                Map<String,Object> data = ds.getData();
                                String eventname = (String) data.get("eventname");
                                String downloadURL = (String) data.get("url");
                                String location = (String) data.get("location");
                                String date = (String) data.get("date");
                                String hour = (String) data.get("hour");
                                String info = (String) data.get("info");
                                String uuid = (String) data.get("postID") ;
                                String latitude = (String) data.get("latitude");
                                String longitude = (String) data.get("longitude");
                                String price = (String) data.get("price");
                                String count = (String) data.get("count");
                                String organizerID = (String) data.get("organizerID");
                                String starCount = (String) data.get("starCount");
                                CategoryItem item = new CategoryItem(eventname,location,date,downloadURL,info,hour,uuid,latitude,longitude,price,count,organizerID,starCount);
                                itemArrayList.add(item);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    return v;
    }
}