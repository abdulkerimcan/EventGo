package com.furkankerim.eventgo.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.CategoryAdapter;
import com.furkankerim.eventgo.Adapters.SearchAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class SearchFragment extends Fragment {
    private View v;
    private EditText searchEditText;
    private String text;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mRecycler;
    private SearchAdapter mAdapter;
    private CategoryItem item;
    private ArrayList<CategoryItem> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = v.findViewById(R.id.search_EditText);
        text = searchEditText.getText().toString();
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = v.findViewById(R.id.recyclerview_seach);
        items = new ArrayList<>();
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(layoutManager);

        mAdapter = new SearchAdapter(items,getContext());
        mRecycler.setAdapter(mAdapter);



        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = searchEditText.getText().toString();
                search(word);

                if(searchEditText.getText().toString().equals("")){
                    items.clear();
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = searchEditText.getText().toString();
                if (TextUtils.isEmpty(word)) {
                    items.clear();
                    mAdapter.notifyDataSetChanged();
                }
                if(searchEditText.getText().toString().equals("")){
                    items.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        return v;
    }

    private void  search(String word) {
        items.clear();
        mAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(word) && !searchEditText.getText().toString().equals("")) {
            mFirestore.collection("Posts").orderBy("eventname").startAt(word.toUpperCase()).endAt(word.toUpperCase() +"\uf8ff")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        items.clear();
                        for (QueryDocumentSnapshot ds : task.getResult()) {
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

                            item = new CategoryItem(eventname, location, date, downloadURL, info, hour,uuid,latitude,longitude,price,count,organizerID,starCount);
                            items.add(item);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else {
                        Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}