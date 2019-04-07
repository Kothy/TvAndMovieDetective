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
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private ArrayList<FriendsItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;

    public FriendsAdapter(Context ctx, ArrayList<FriendsItem> imageModelArrayList, FragmentManager fm, Activity activity){
        this.contex=ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm=fm;
        this.activity=activity;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friend_item, parent, false);

        FriendsAdapter.ViewHolder holder = new FriendsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {

        holder.nickname.setText(items.get(position).nickname);
        holder.followButton.setOnClickListener(click ->{
            //Toast.makeText(contex, "followuj kamoša", Toast.LENGTH_SHORT).show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = database.getReference("users/"+MainActivity.mail.replace(".","_")+"/settings/friends");
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(items.get(position).email, items.get(position).nickname);
            dbRef.updateChildren(childUpdates);

        });
        holder.parentLayout.setOnClickListener(click -> {
            //Toast.makeText(contex, "presmerovanie na profil", Toast.LENGTH_SHORT).show();
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
        ConstraintLayout parentLayout;
        Button followButton;

        public ViewHolder(View itemView) {
            super(itemView);
            followButton = (Button) itemView.findViewById(R.id.button10);
            nickname = (TextView) itemView.findViewById(R.id.friend_name);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layoutItem4);
        }
    }
}
