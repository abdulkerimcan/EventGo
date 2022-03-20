package com.furkankerim.eventgo.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.furkankerim.eventgo.Activities.SignUpAndSignInActivity;
import com.furkankerim.eventgo.Adapters.UsersAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.Models.Chat;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class ChatFragment extends Fragment {
    private View v;
    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    private UsersAdapter mAdapter;
    private ArrayList<Chat> userArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v  = inflater.inflate(R.layout.fragment_chat, container, false);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userArrayList = new ArrayList<>();
        mRecycler = v.findViewById(R.id.chat_recyclcerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new UsersAdapter(userArrayList,getContext());
        mRecycler.setAdapter(mAdapter);

        if (mUser == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Warning");
            builder.setMessage("You must log in!");
            builder.setCancelable(false);
            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getContext(), SignUpAndSignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                }
            });
            dialog = builder.create();
            dialog.show();
            Button buttonbackground = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            buttonbackground.setTextColor( getContext().getResources().getColor(R.color.iconSelected,getContext().getTheme()));

            Button buttonbackground1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonbackground1.setTextColor( getContext().getResources().getColor(R.color.iconSelected,getContext().getTheme()));
        }else {
            mFirestore.collection("User").document(mUser.getUid()).collection("Channel")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot ds :queryDocumentSnapshots.getDocuments()) {
                        Map<String,Object> mData = ds.getData();
                        String channelID = (String) mData.get("channelID");
                        String userID = (String) mData.get("userID");
                        String userImg = (String) mData.get("userImg");
                        String username = (String) mData.get("username");
                        Chat chat = new Chat(channelID,userID,userImg,username);
                        userArrayList.add(chat);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }




        return v;

    }
}