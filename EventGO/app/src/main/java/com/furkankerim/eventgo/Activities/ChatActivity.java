package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.ChatAdapter;
import com.furkankerim.eventgo.Fragments.ChatFragment;
import com.furkankerim.eventgo.Models.Message;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView usernametxt;
    private Intent intent;
    private EditText sendEdit;
    private String hedefID,channelID = "";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Message message;
    private User user;

    private ArrayList<Message> messageArrayList;
    private RecyclerView mRecycler;
    private ChatAdapter mAdapter;

    private void init() {
        circleImageView = findViewById(R.id.chat_activity_circleImage);
        usernametxt = findViewById(R.id.chat_activity_textViewChatName);
        sendEdit = findViewById(R.id.chat_activity_editMessage);
        intent = getIntent();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        messageArrayList = new ArrayList<>();
        mRecycler = findViewById(R.id.chat_activity_recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        mRecycler.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        mAdapter = new ChatAdapter(messageArrayList,getApplicationContext(),mUser.getUid(),intent.getStringExtra("hedefUrl"));
        mRecycler.setAdapter(mAdapter);
        getCurrentUser();
        hedefID = intent.getStringExtra("hedefID");
        usernametxt.setText(intent.getStringExtra("hedefUsername"));
        channelID = intent.getStringExtra("channelID");
        Picasso.get().load(intent.getStringExtra("hedefUrl")).into(circleImageView);


        if (!channelID.equals("")) {
            mFirestore.collection("ChatChannels").document(channelID).collection("Messages").orderBy("time", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null){
                                Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                messageArrayList.clear();
                                for (DocumentSnapshot ds : value.getDocuments()) {
                                    // Map<String,Object> mData = ds.getData();
                                    message = ds.toObject(Message.class);
                                    if (message != null) {
                                        messageArrayList.add(message);
                                        mAdapter.notifyDataSetChanged();
                                        mRecycler.scrollToPosition(messageArrayList.size() - 1);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private User getCurrentUser() {
        if (mUser != null) {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> data = value.getData();
                        if (data != null) {
                            String usernametxt = (String) data.get("username");
                            String email = (String) data.get("email");
                            String url = (String) data.get("downloadUrl");
                            boolean organizer = (boolean) data.get("organizer");
                            user = new User(email, usernametxt, url, organizer);
                        }
                    }
                }
            });
            return user;
        }
        return null;
    }
    public void btnCancel(View view) {
        onBackPressed();
    }

    public void btnSend(View view) {

        String message = sendEdit.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            Map<String,Object> data = new HashMap<>();
            data.put("time",FieldValue.serverTimestamp());
            Message msg = new Message(hedefID,mUser.getUid(),message,"text");
            data.put("recipient",hedefID);
            data.put("sender",mUser.getUid());
            data.put("content",message);
            data.put("type","text");
            data.put("time",FieldValue.serverTimestamp());

            UUID uuid = UUID.randomUUID();
            mFirestore.collection("ChatChannels").document(channelID).collection("Messages").document(uuid.toString())
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendEdit.setText("");
                    messageArrayList.add(msg);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}