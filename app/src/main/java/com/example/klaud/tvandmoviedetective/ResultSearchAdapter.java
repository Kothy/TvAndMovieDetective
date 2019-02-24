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
import android.widget.ImageView;;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ResultSearchAdapter extends RecyclerView.Adapter<ResultSearchAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private ArrayList<MovieItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;

    public ResultSearchAdapter(Context ctx, ArrayList<MovieItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;
    }
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_search_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.time.setText(items.get(position).getName());
        if (items.get(position).release_date==null){
            holder.year.setText("");
        } else holder.year.setText(items.get(position).release_date);

        if (items.get(position).getPoster_path().equals("null")) holder.iv.setImageResource(R.drawable.nopicture);
        else {
            String url=String.format("https://image.tmdb.org/t/p/w300%s", items.get(position).getPoster_path());
            Picasso.get().load(url).into(holder.iv);
        }
        holder.parentLayout.setOnClickListener(click ->{
            Fragment fragment = null;
            fragment = new MovieDetail();
            Bundle bundle = new Bundle();
            bundle.putString("id", items.get(position).getId().toString());
            bundle.putString("title",items.get(position).getName());

            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView time,year;
        ImageView iv;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            year = itemView.findViewById(R.id.yearTextView);
            time = (TextView) itemView.findViewById(R.id.movie_title);
            iv = (ImageView) itemView.findViewById(R.id.itemImage2);
            parentLayout = itemView.findViewById(R.id.parent_layoutItem);

        }
    }
}
