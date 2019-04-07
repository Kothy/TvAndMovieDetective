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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private ArrayList<MovieItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;

    public MovieAdapter(Context ctx, ArrayList<MovieItem> imageModelArrayList, FragmentManager fm, Activity activity){
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
        View view = inflater.inflate(R.layout.recycler_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.get(position).getPoster_path()!=null && items.get(position).getPoster_path().equals("null")){
            holder.iv.setImageResource(items.get(position).getImage_drawable());
        } else {
            String url=String.format("https://image.tmdb.org/t/p/w300%s", items.get(position).getPoster_path());
            Picasso.get().load(url).into(holder.iv);
        }

        holder.time.setText(items.get(position).getName());
        holder.parentLayout.setOnClickListener(click ->{
            //Toast.makeText(contex, items.get(position).getName(), Toast.LENGTH_LONG).show();
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
        /*holder.parentLayout.setOnLongClickListener((click) ->{
            SharedPreferences prefs= MainActivity.prefs;
            if (prefs.getString("class","").equals("MyMovies") || prefs.getString("class","").equals("MyMoviesWatched")){

                String mai=MainActivity.mail.replace(".","_");
                FirebaseDatabase.getInstance().getReference("/users/"+mai+"/movies/"+items.get(position).getId()).removeValue();

                items.remove(position);
                notifyDataSetChanged();
                if (MyMovies.recycler != null) MyMovies.recycler.invalidate();
                if (MyMoviesWatched.recycler != null) MyMoviesWatched.recycler.invalidate();
            }
            return true;
        });*/

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