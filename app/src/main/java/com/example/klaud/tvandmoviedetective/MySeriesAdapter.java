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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MySeriesAdapter extends RecyclerView.Adapter<MySeriesAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<SeriesItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;
    private int position;

    public MySeriesAdapter(Context ctx, ArrayList<SeriesItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;

    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    @Override
    public MySeriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_shows_recycler_item, parent, false);

        MySeriesAdapter.ViewHolder holder = new MySeriesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MySeriesAdapter.ViewHolder holder, int position) {

        holder.second.setText(items.get(position).network);
        if (items.get(position).lastSeen==null){
            holder.third.setText("Not seen any episode");
        } else {
            holder.third.setText("Last seen episode: "+items.get(position).lastSeen);
        }

        if (items.get(position).equals("null")){
            holder.iv.setImageResource(R.drawable.nopicture);
        } else {
            String url=String.format("https://image.tmdb.org/t/p/w300%s", items.get(position).getPoster_path());
            Picasso.get().load(url).into(holder.iv);
        }

        holder.title.setText(items.get(position).getName());

        holder.parentLayout.setOnClickListener(click -> {

            Fragment fragment;
            fragment = new SeriesDetails();
            Bundle bundle = new Bundle();
            bundle.putString("id", items.get(position).getId().toString());

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView title, second, third;
        ImageView iv;
        ConstraintLayout parentLayout;
        //Button contextB;

        public ViewHolder(View itemView) {
            super(itemView);
            //contextB = (Button) itemView.findViewById(R.id.button8);
            second = (TextView) itemView.findViewById(R.id.textView6);
            third = (TextView) itemView.findViewById(R.id.seaAndEpNum2);
            title = (TextView) itemView.findViewById(R.id.season2);
            iv = (ImageView) itemView.findViewById(R.id.itemImage2);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layoutItem2);
            parentLayout.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //menuInfo is null
            menu.add(Menu.NONE, 1, getAdapterPosition(), "Remove");
            menu.add(Menu.NONE, 2, getAdapterPosition(), "Mark next episode as watched");
        }
    }
}
