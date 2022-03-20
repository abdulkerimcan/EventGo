package com.furkankerim.eventgo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.Models.Ticket;
import com.furkankerim.eventgo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketHolder> {
    private ArrayList<Ticket> tickets;
    private Context context;
    private Ticket ticket;
    private View view;

    public TicketAdapter(ArrayList<Ticket> tickets, Context context) {
        this.tickets = tickets;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.myticket_item_design,parent,false);
        return new TicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketHolder holder, int position) {
        ticket = tickets.get(position);
        CategoryItem event = ticket.getItem();
        holder.title.setText(event.getTitle());
        holder.location.setText(event.getLocation());
        holder.date.setText(event.getDate());
        holder.hour.setText(event.getHour());
        holder.student.setText("Student: "+ticket.getStudent());
        holder.fullfare.setText("Full fare: " + ticket.getFullfare());
        if (!ticket.getDrink().matches("Select Drink")) {
            holder.drinkCount.setText("Count: " + ticket.getDrinkCount());
            holder.drink.setText("Drink: "+ ticket.getDrink());
        }else {
            holder.drinkCount.setVisibility(View.GONE);
            holder.drink.setVisibility(View.GONE);
        }

        if (!ticket.getSnack().matches("Select Snack")) {
            holder.snackCount.setText("Count: " + ticket.getSnackCount());
            holder.snack.setText("Snack: " +ticket.getSnack());
        }else {
            holder.snackCount.setVisibility(View.GONE);
            holder.snack.setVisibility(View.GONE);
        }

        if (!ticket.getProduct().matches("Select product")) {
            holder.product.setText("Product: "+ticket.getProduct());
        }else {
            holder.product.setVisibility(View.GONE);
        }

        Picasso.get().load(event.getUrl()).resize(150,130).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ticket tct = tickets.get(position);
                CategoryItem item = tct.getItem();
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("item",item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    class TicketHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,location,date,hour,student,fullfare,drink,drinkCount,snack,snackCount,product;
        public TicketHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ticketimg);
            title = itemView.findViewById(R.id.ticketTitle);
            location = itemView.findViewById(R.id.ticketlocation);
            date = itemView.findViewById(R.id.ticketdate);
            hour = itemView.findViewById(R.id.tickethour);
            student = itemView.findViewById(R.id.ticketstudent);
            fullfare = itemView.findViewById(R.id.ticketfullfare);
            drink = itemView.findViewById(R.id.ticketdrink);
            drinkCount = itemView.findViewById(R.id.ticketdrinkCount);
            snack = itemView.findViewById(R.id.ticketsnack);
            snackCount = itemView.findViewById(R.id.ticketsnackCount);
            product = itemView.findViewById(R.id.ticketProduct);

        }
    }
}
