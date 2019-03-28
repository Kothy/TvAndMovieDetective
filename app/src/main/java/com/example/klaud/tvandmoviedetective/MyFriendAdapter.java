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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private ArrayList<FriendsItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;


    public MyFriendAdapter(Context ctx, ArrayList<FriendsItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;
    }

    @Override
    public MyFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_friend_item, parent, false);


        MyFriendAdapter.ViewHolder holder = new MyFriendAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyFriendAdapter.ViewHolder holder, int position) {
        holder.nickname.setText(items.get(position).nickname);
        holder.parentLayout.setOnClickListener(click -> {
            Toast.makeText(contex, "presmerovanie na nahlad pouyivatela", Toast.LENGTH_SHORT).show();

            Fragment fragment;
            fragment = new UserProfile();
            Bundle bundle = new Bundle();
            bundle.putString("email", items.get(position).email);
            bundle.putString("nickname",items.get(position).nickname);


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

        TextView nickname;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            nickname = (TextView) itemView.findViewById(R.id.friends_name);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layoutItem55);
        }


    }
}
