package com.furkankerim.eventgo.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Activities.SignUpAndSignInActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.categoryHolder> {
    private ArrayList<CategoryItem> categoryItems;
    private Context context;
    private CategoryItem categoryItem;
    private View view;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference mRef;
    private Query mQuery;
    private Map<String,Object> mData;
    private AlertDialog dialog;
    public CategoryAdapter(ArrayList<CategoryItem> categoryItems,Context context) {
        this.categoryItems = categoryItems;
        this.context = context;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mQuery = mFirestore.collection("Favorites").document(mUser.getUid()).collection("FavoriteItems");
        }
    }


    @NonNull
    @Override
    public categoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false);
        categoryHolder item = new categoryHolder(view);

        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull categoryHolder holder, int position) {

        categoryItem = categoryItems.get(position);
        holder.title.setText(categoryItem.getTitle());
        holder.location.setText(categoryItem.getLocation());
        holder.hour.setText(categoryItem.getHour());
        holder.date.setText(categoryItem.getDate());
        holder.price.setText(categoryItem.getPrice() + " ₺");
        holder.ratingBar.setRating(Float.valueOf(categoryItem.getStarCount()));
        Picasso.get().load(categoryItem.getUrl()).resize(150,130).into(holder.imageView);

        if (mUser != null) {
            mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    CategoryItem newItem = categoryItems.get(position);
                    for (DocumentSnapshot ss : value.getDocuments()) {
                        String id = ss.getId();
                        if (id.matches(newItem.getPostID())) {
                            holder.favorite.setChecked(true);
                        }
                    }
                }

            });
            if (categoryItem.getOrganizerID().matches(mUser.getUid())) {
                holder.favorite.setVisibility(View.GONE);
            }else {
                holder.favorite.setVisibility(View.VISIBLE);
            }
        }

        /*
        if (mUser != null) {
            holder.favorite.setEnabled(true);
        }else {
            holder.favorite.setEnabled(false);
        }

         */

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUser != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        if (holder.favorite.isChecked()) {
                            CategoryItem newItem = categoryItems.get(position);
                            mRef = mFirestore.collection("Posts").document(newItem.getPostID());
                            mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null && value.getData() == null) {
                                        Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mData = value.getData();
                                    mData.put("addingTime", FieldValue.serverTimestamp());
                                    mFirestore.collection("Favorites").document(mUser.getUid()).collection("FavoriteItems")
                                    .document(newItem.getPostID()).set(mData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Favorilere Eklendi", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });

                        } else {
                            CategoryItem item = categoryItems.get(position);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Warning");

                            builder.setMessage("Are you sure you want to remove " +  item.getTitle() + " from favorites?");
                            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.favorite.setChecked(true);
                                }
                            });
                            builder.setCancelable(false);
                            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CategoryItem newItem = categoryItems.get(position);
                                    mFirestore.collection("Favorites").document(mUser.getUid()).collection("FavoriteItems").document(newItem.getPostID())
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "KALDIRLDI", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });


                            dialog = builder.create();
                            dialog.show();
                            Button buttonbackground = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            buttonbackground.setTextColor( context.getResources().getColor(R.color.iconSelected,context.getTheme()));

                            Button buttonbackground1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            buttonbackground1.setTextColor( context.getResources().getColor(R.color.iconSelected,context.getTheme()));


                        }
                    }
                }else {
                    holder.favorite.setBtnFillColor(Color.rgb(170,170,170));
                    holder.favorite.setChecked(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Warning");

                    builder.setMessage("Please, log in to add to favorites.");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Sign in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.favorite.setChecked(false);
                            Intent intent = new Intent(context, SignUpAndSignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent);
                        }
                    });
                    builder.setPositiveButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.favorite.setChecked(false);
                        }
                    });


                    dialog = builder.create();
                    dialog.show();
                    Button buttonbackground = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonbackground.setTextColor( context.getResources().getColor(R.color.iconSelected,context.getTheme()));

                    Button buttonbackground1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonbackground1.setTextColor( context.getResources().getColor(R.color.iconSelected,context.getTheme()));

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryItem newItem = categoryItems.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("item",newItem);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    class categoryHolder extends RecyclerView.ViewHolder{
        ShineButton favorite;
        ImageView imageView;
        TextView title,location,date,hour,price;
        RatingBar ratingBar;
        int kPos;
        public categoryHolder(@NonNull View itemView) {
            super(itemView);



            imageView = itemView.findViewById(R.id.layout_category_item_img);
            favorite = itemView.findViewById(R.id.layout_category_item_favorite);
            title = itemView.findViewById(R.id.layout_category_item_title);
            location = itemView.findViewById(R.id.layout_category_item_location);
            date = itemView.findViewById(R.id.layout_category_item_date);
            hour = itemView.findViewById(R.id.layout_category_item_hour);
            ratingBar = itemView.findViewById(R.id.category_item_ratingbar);
            price = itemView.findViewById(R.id.layout_category_item_price);
            kPos = getAdapterPosition();


        }
    }
}
