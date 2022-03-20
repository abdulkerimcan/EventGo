package com.furkankerim.eventgo.Fragments;

import android.content.Context;
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
import com.furkankerim.eventgo.Models.Ticket;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyTicketsFragment extends Fragment {
    private View v;
    private RecyclerView mRecycler;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TicketAdapter mAdapter;
    private ArrayList<Ticket> ticketArrayList;
    private CategoryItem item;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v =  inflater.inflate(R.layout.fragment_my_tickets, container, false);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRecycler = v.findViewById(R.id.recyclervievMyTickets);
        ticketArrayList = new ArrayList<>();
        mAdapter = new TicketAdapter(ticketArrayList,getContext());

        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecycler.setAdapter(mAdapter);


        mFirestore.collection("Tickets").document(mUser.getUid())
                .collection("posts")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
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

                            String drink = (String) data.get("drink");
                            String drinkCount = (String) data.get("drinkCount");
                            String snack = (String) data.get("snack");
                            String snackCount = (String) data.get("snackCount");
                            String product = (String) data.get("product");
                            String student = (String) data.get("student");
                            String fullfare = (String) data.get("fullfare");
                            CategoryItem citem = new CategoryItem(eventname,location,date,downloadURL,info,hour,uuid,latitude,longitude,price,count,organizerID,starCount);
                            Ticket ticket = new Ticket(student,fullfare,drink,drinkCount,
                                    snack,snackCount,product,citem);
                            ticketArrayList.add(ticket);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
        return v;
    }
}