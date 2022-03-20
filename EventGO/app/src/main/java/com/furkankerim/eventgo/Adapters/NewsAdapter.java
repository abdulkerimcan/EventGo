package com.furkankerim.eventgo.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.furkankerim.eventgo.Models.News;
import com.furkankerim.eventgo.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends SliderViewAdapter<NewsAdapter.NewsHolder> {
    private ArrayList<News> newsList;
    private View view;
    private News news;
    private Dialog newsDialog;
    private Context context;
    private ImageView bigImg;
    private CircleImageView circleImageView;
    private TextView baslik, date, text;


    public NewsAdapter(ArrayList<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    private void messageSendDialog(News news) {
        newsDialog = new Dialog(context, android.R.style.Theme_NoTitleBar);
        newsDialog.setContentView(R.layout.news_dialog_1);
        bigImg = newsDialog.findViewById(R.id.news_bigImg);
        circleImageView = newsDialog.findViewById(R.id.dialog_img1);
        baslik = newsDialog.findViewById(R.id.textView_baslik1);
        date = newsDialog.findViewById(R.id.textView_tarih1);
        text = newsDialog.findViewById(R.id.dialog_textview1);

        Picasso.get().load(news.getImg()).into(bigImg);
        Picasso.get().load(news.getCircleImg()).into(circleImageView);

        baslik.setText(news.getTitle());
        date.setText(news.getDate());
        text.setText(news.getInfo());
        newsDialog.show();

    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);

        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsHolder viewHolder, int position) {
        news = newsList.get(position);
        viewHolder.baslik.setText(news.getTitle());
        Picasso.get().load(news.getImg()).into(viewHolder.baslikimg);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSendDialog(newsList.get(position));
            }
        });
    }

    @Override
    public int getCount() {
        return newsList.size();
    }


    class NewsHolder extends SliderViewAdapter.ViewHolder {
        TextView baslik;
        ImageView baslikimg;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            baslik = itemView.findViewById(R.id.news_item_tv_baslik);
            baslikimg = itemView.findViewById(R.id.news_item_baslikImg);
        }
    }
}

