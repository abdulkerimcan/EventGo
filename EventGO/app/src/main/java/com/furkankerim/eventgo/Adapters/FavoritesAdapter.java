package com.furkankerim.eventgo.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {
    private ArrayList<CategoryItem> favoriteItems;
    private Context context;
    private CategoryItem favoriteItem;
    private View view;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Query mQuery;
    private AlertDialog dialog;


    public FavoritesAdapter(ArrayList<CategoryItem> favoriteItems, Context context) {
        this.favoriteItems = favoriteItems;
        this.context = context;

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false);
        return new FavoritesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesHolder holder, int position) {


        favoriteItem = favoriteItems.get(position);
        holder.title.setText(favoriteItem.getTitle());
        holder.location.setText(favoriteItem.getLocation());
        holder.favorite.setChecked(true);
        holder.hour.setText(favoriteItem.getHour());
        holder.date.setText(favoriteItem.getDate());
        holder.ratingBar.setRating(Float.parseFloat(favoriteItem.getStarCount()));
        Picasso.get().load(favoriteItem.getUrl()).into(holder.imageView);
        if (!holder.favorite.isChecked()) {
            holder.favorite.setEnabled(false);
        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryItem item = favoriteItems.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to remove " + item.getTitle() + " from favorites?");
                CategoryItem newItem = favoriteItems.get(position);
                builder.setCancelable(false);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirestore.collection("Favorites").document(mUser.getUid()).collection("FavoriteItems").document(newItem.getPostID())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.favorite.setChecked(true);
                    }
                });


                dialog = builder.create();
                dialog.show();
                Button buttonbackground = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonbackground.setTextColor(context.getResources().getColor(R.color.iconSelected, context.getTheme()));

                Button buttonbackground1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonbackground1.setTextColor(context.getResources().getColor(R.color.iconSelected, context.getTheme()));

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryItem newFav = favoriteItems.get(position);

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("item", newFav);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    class FavoritesHolder extends RecyclerView.ViewHolder {
        ShineButton favorite;
        ImageView imageView;
        TextView title, location, date, hour;
        RatingBar ratingBar;
        int kPos;

        public FavoritesHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.layout_category_item_img);
            favorite = itemView.findViewById(R.id.layout_category_item_favorite);
            title = itemView.findViewById(R.id.layout_category_item_title);
            location = itemView.findViewById(R.id.layout_category_item_location);
            date = itemView.findViewById(R.id.layout_category_item_date);
            hour = itemView.findViewById(R.id.layout_category_item_hour);
            ratingBar = itemView.findViewById(R.id.category_item_ratingbar);
            kPos = getAdapterPosition();
        }
    }
}
