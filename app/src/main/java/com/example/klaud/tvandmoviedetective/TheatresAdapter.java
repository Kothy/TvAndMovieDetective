package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TheatresAdapter extends RecyclerView.Adapter<TheatresAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<TheatresItem> items;
    private Context contex;

    public TheatresAdapter(Context ctx, ArrayList<TheatresItem> imageModelArrayList){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
    }

    @Override
    public TheatresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.theatre_recycler_item, parent, false);
        TheatresAdapter.ViewHolder holder = new TheatresAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TheatresAdapter.ViewHolder holder, int position) {
        holder.length.setText(items.get(position).length);
        holder.date.setText(items.get(position).date);
        holder.pg.setText(items.get(position).pg);
        holder.times.setText(items.get(position).times.toString());
        holder.title.setText(items.get(position).title);
        //holder.cl.setBackgroundColor(contex.getResources().getColor(R.color.gray));
        holder.karticka.setOnClickListener( click ->{
            Toast.makeText(contex, "kliknute na film", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, length, pg, times,date;
        CardView karticka;
        ConstraintLayout cl;

        public ViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.parent_layoutItem3);
            karticka = itemView.findViewById(R.id.karticka);
            title = itemView.findViewById(R.id.friend_name);
            length = itemView.findViewById(R.id.lenght);
            pg = itemView.findViewById(R.id.pg);
            times = itemView.findViewById(R.id.times_the);
            date = itemView.findViewById(R.id.theatre_date);
        }
    }
}