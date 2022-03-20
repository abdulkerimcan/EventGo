package com.furkankerim.eventgo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryMainAdapter extends RecyclerView.Adapter<CategoryMainAdapter.CategoryHolder> {
    private ArrayList<CategoryItem> categoryItems;
    private Context context;
    private View view;
    private CategoryItem categoryItem;

    public CategoryMainAdapter(ArrayList<CategoryItem> categoryItems, Context context) {
        this.categoryItems = categoryItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.category_item_design,parent,false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        categoryItem = categoryItems.get(position);
        holder.title.setText(categoryItem.getTitle());
        holder.location.setText(categoryItem.getLocation());
        holder.price.setText(categoryItem.getPrice() + " ₺");
        holder.date.setText(categoryItem.getDate());
        holder.ratingBar.setRating(Float.valueOf(categoryItem.getStarCount()));
        Picasso.get().load(categoryItem.getUrl()).resize(140,155).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryItem newItem = categoryItems.get(position);

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("item", newItem);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,location,date,price;
        RatingBar ratingBar;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.concerts_item_design_img);
            title = itemView.findViewById(R.id.concerts_item_design_title);
            location = itemView.findViewById(R.id.concerts_item_design_location);
            date = itemView.findViewById(R.id.concerts_item_design_date);
            ratingBar = itemView.findViewById(R.id.concerts_item_design_rating);
            price = itemView.findViewById(R.id.main_price);
        }
    }
}
