package com.furkankerim.eventgo.Fragments;


import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.CategoryMainAdapter;
import com.furkankerim.eventgo.Adapters.NewsAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.Models.News;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment {
    private View v;
    private RecyclerView concertsRecyler, theatersRecyler, museumRecyler,top10RecyclerView;
    private NewsAdapter newsAdapter;
    private CategoryMainAdapter concertAdapter, theatersAdapter, museumAdapter,top10Adapter;
    private ArrayList<CategoryItem> concertItems, theatersItems, museumItems,top10items;
    private ArrayList<News> newsArrayList;
    private DatabaseReference reference;

    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);


        newsArrayList = new ArrayList<>();
        theatersRecyler = v.findViewById(R.id.theaters_recylerView);
        museumRecyler = v.findViewById(R.id.museum_recylerView);
        concertsRecyler = v.findViewById(R.id.concerts_recylerView);
        top10RecyclerView = v.findViewById(R.id.top10_recylerView);

        mFirestore = FirebaseFirestore.getInstance();


        reference = FirebaseDatabase.getInstance("https://eventgo-7ed7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Slider");

        setRecyclers();

        orderStars();

        getDataFirestore();

        SliderView sliderView = v.findViewById(R.id.imageSlider);
        newsAdapter = new NewsAdapter(newsArrayList, v.getContext());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsArrayList.clear();
                for (DataSnapshot snp : snapshot.getChildren()) {
                    News newsObject = snp.getValue(News.class);
                    newsArrayList.add(newsObject);
                }
                sliderView.setSliderAdapter(newsAdapter);
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sliderView.startAutoCycle();
        sliderView.setAutoCycle(true);
        sliderView.setSliderTransformAnimation(SliderAnimations.HORIZONTALFLIPTRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        orderStars();
    }

    private void orderStars () {
        mFirestore.collection("Posts").orderBy("starCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        top10items.clear();
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                Map<String,Object> data = ds.getData();
                                String eventname = (String) data.get("eventname");
                                String downloadURL = (String) data.get("url");
                                String location = (String) data.get("location");
                                String date = (String) data.get("date");
                                String hour = (String) data.get("hour");
                                String info = (String) data.get("info");
                                System.out.println("girdi laaa");
                                String uuid = (String) data.get("postID");
                                String latitude = (String) data.get("latitude");
                                String longitude = (String) data.get("longitude");
                                String price = (String) data.get("price");
                                String count = (String) data.get("count");
                                String organizerID = (String) data.get("organizerID");
                                String starCount = (String) data.get("starCount");
                                CategoryItem item = new CategoryItem(eventname,location,date,downloadURL,info,hour,uuid,latitude,longitude,price,count,organizerID,starCount);
                                top10items.add(item);
                                top10Adapter.notifyDataSetChanged();
                            }
                        }
                });
    }

    private void setRecyclers() {

        top10RecyclerView.setHasFixedSize(true);
        top10RecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(),RecyclerView.HORIZONTAL,false));
        top10items = new ArrayList<>();
        top10Adapter = new CategoryMainAdapter(top10items,getContext());
        top10RecyclerView.setAdapter(top10Adapter);
        top10RecyclerView.addItemDecoration(new SpacesItemDecoration(30));
        top10RecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 30, false));

        concertsRecyler.setHasFixedSize(true);
        concertsRecyler.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
        concertItems = new ArrayList<>();

        concertAdapter = new CategoryMainAdapter(concertItems, v.getContext());
        concertsRecyler.setAdapter(concertAdapter);
        concertsRecyler.addItemDecoration(new SpacesItemDecoration(30));
        concertsRecyler.addItemDecoration(new GridSpacingItemDecoration(1, 30, false));

        theatersRecyler.setHasFixedSize(true);
        theatersRecyler.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
        theatersItems = new ArrayList<>();
        theatersAdapter = new CategoryMainAdapter(theatersItems, v.getContext());
        theatersRecyler.setAdapter(theatersAdapter);
        theatersRecyler.addItemDecoration(new SpacesItemDecoration(30));
        theatersRecyler.addItemDecoration(new GridSpacingItemDecoration(1, 30, false));

        museumRecyler.setHasFixedSize(true);
        museumRecyler.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
        museumItems = new ArrayList<>();

        museumAdapter = new CategoryMainAdapter(museumItems, v.getContext());
        museumRecyler.setAdapter(museumAdapter);
        museumRecyler.addItemDecoration(new SpacesItemDecoration(30));
        museumRecyler.addItemDecoration(new GridSpacingItemDecoration(1, 30, false));
    }

    private void getDataFirestore() {
        mFirestore.collection("Posts").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(v.getContext(), error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    concertItems.clear();
                    theatersItems.clear();
                    museumItems.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data.size() > 0) {
                            String eventname = (String) data.get("eventname");
                            String downloadURL = (String) data.get("url");
                            String location = (String) data.get("location");
                            String date = (String) data.get("date");
                            String hour = (String) data.get("hour");
                            String info = (String) data.get("info");
                            String category = (String) data.get("category");
                            String uuid = (String) data.get("postID");
                            String latitude = (String) data.get("latitude");
                            String longitude = (String) data.get("longitude");
                            String price = (String) data.get("price");
                            String count = (String) data.get("count");
                            String organizerID = (String) data.get("organizerID");
                            String starCount = (String) data.get("starCount");

                            CategoryItem categoryItem = new CategoryItem(eventname, location, date, downloadURL, info, hour,uuid,latitude,longitude,price,count,organizerID,starCount);



                            if (category.matches("Concerts")) {
                                concertItems.add(categoryItem);
                                concertAdapter.notifyDataSetChanged();
                            } else if (category.matches("Theaters")) {
                                theatersItems.add(categoryItem);
                                theatersAdapter.notifyDataSetChanged();
                            } else if (category.matches("Museum")) {
                                museumItems.add(categoryItem);
                                museumAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }
}


class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}

class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; //Number of columns
    private int spacing; //interval
    private boolean includeEdge; //Whether to include edges

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //Here is the key, you need to judge according to how many columns you have
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}
