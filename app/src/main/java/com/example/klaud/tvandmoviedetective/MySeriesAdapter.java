package com.example.klaud.tvandmoviedetective;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MySeriesAdapter extends RecyclerView.Adapter<MySeriesAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<SeriesItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;

    public MySeriesAdapter(Context ctx, ArrayList<SeriesItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;
    }

    @Override
    public MySeriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.my_shows_recycler_item, parent, false);
        MySeriesAdapter.ViewHolder holder = new MySeriesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MySeriesAdapter.ViewHolder holder, int position) {

        holder.iv.setImageResource(items.get(position).getImage_drawable());
        holder.title.setText(items.get(position).getName());
        holder.parentLayout.setOnClickListener(click ->{

            Toast.makeText(contex, "klikla som na polozku v myshows", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, second, third;
        ImageView iv;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            second = (TextView) itemView.findViewById(R.id.textView6);
            third = (TextView) itemView.findViewById(R.id.seaAndEpNum2);
            title = (TextView) itemView.findViewById(R.id.season2);
            iv = (ImageView) itemView.findViewById(R.id.itemImage2);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layoutItem2);
        }


    }
}
