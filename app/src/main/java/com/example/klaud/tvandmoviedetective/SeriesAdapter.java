package com.example.klaud.tvandmoviedetective;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import java.util.ArrayList;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<SeriesItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;
    public SeriesAdapter(Context ctx, ArrayList<SeriesItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.iv.setImageResource(items.get(position).getImage_drawable());
        holder.time.setText(items.get(position).getName());
        holder.parentLayout.setOnClickListener(click ->{
            //Toast.makeText(contex, items.get(position).getName(), Toast.LENGTH_LONG).show();
            Fragment fragment = null;
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

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView time;
        ImageView iv;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.tvTitle);
            iv = (ImageView) itemView.findViewById(R.id.itemImage);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layoutItem);
        }


    }
}
