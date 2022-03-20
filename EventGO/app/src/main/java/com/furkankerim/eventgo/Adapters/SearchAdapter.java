package com.furkankerim.eventgo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class SearchAdapter  extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
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

    public SearchAdapter(ArrayList<CategoryItem> categoryItems, Context context) {
        this.categoryItems = categoryItems;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false);
        SearchHolder item = new SearchHolder(view);

        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        categoryItem = categoryItems.get(position);
        holder.title.setText(categoryItem.getTitle());
        holder.location.setText(categoryItem.getLocation());
        holder.hour.setText(categoryItem.getHour());
        holder.date.setText(categoryItem.getDate());
        holder.price.setText(categoryItem.getPrice() + " ₺");
        holder.ratingBar.setRating(Float.valueOf(categoryItem.getStarCount()));
        holder.favorite.setVisibility(View.INVISIBLE);
        Picasso.get().load(categoryItem.getUrl()).resize(150,130).into(holder.imageView);

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

    class SearchHolder extends RecyclerView.ViewHolder {
        ShineButton favorite;
        ImageView imageView;
        TextView title,location,date,hour,price;
        RatingBar ratingBar;
        int kPos;
        public SearchHolder(@NonNull View itemView) {
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
