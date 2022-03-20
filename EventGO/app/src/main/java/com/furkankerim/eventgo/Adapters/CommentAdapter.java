package com.furkankerim.eventgo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.Comment;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private ArrayList<Comment> comments;
    private View view;
    private Comment comment;


    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;


    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);

        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        comment = comments.get(position);
        holder.commenttxt.setText(comment.getComment());
        holder.ratingBar.setRating(Float.valueOf(comment.getRate()));
        holder.username.setText(comment.getUsername());
        holder.date.setText(comment.getDate());

        if (comment.getImg().equals("default")) {
            holder.circleImageView.setImageResource(R.drawable.ic_action_person);
        }else
            Picasso.get().load(comment.getImg()).resize(80,80).into(holder.circleImageView);


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView commenttxt,username,date;
        RatingBar ratingBar;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.comment_item_userimg);
            commenttxt = itemView.findViewById(R.id.comment_item_commenttxt);
            username = itemView.findViewById(R.id.comment_item_usernametxt);
            date = itemView.findViewById(R.id.comment_item_date);
            ratingBar = itemView.findViewById(R.id.commentRatingBar);
        }
    }
}
