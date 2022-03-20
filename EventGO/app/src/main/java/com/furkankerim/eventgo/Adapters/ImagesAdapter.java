package com.furkankerim.eventgo.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Models.Images;
import com.furkankerim.eventgo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageHolder> {
    private ArrayList<Images> images;
    private Context context;
    private View view;
    private Images image;


    public ImagesAdapter(ArrayList<Images> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.images_item,parent,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        image = images.get(position);
        Picasso.get().load(image.getImageURL()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = images.get(position);
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.images_layout);
                ImageView img = dialog.findViewById(R.id.img2);
                Picasso.get().load(image.getImageURL()).resize(300,400).into(img);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageitemView);
        }
    }
}
