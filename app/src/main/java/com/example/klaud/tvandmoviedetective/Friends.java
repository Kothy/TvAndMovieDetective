package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Friends extends Fragment {
    public static Context ctx;
    static DataSnapshot data=null;
    public static RecyclerView recycler, myFriendsRec;
    public static FriendsAdapter adapter;
    public static MyFriendAdapter adapter2;
    public static ArrayList<FriendsItem> items = new ArrayList<>();
    public static ArrayList<FriendsItem> myFriendsItems = new ArrayList<>();
    DatabaseReference dbRef;
    public static String maiil;
    TextView noFriends;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class",MainActivity.prefs.getString("class",""));
        MainActivity.editor.putString("class","Friends");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.friends_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friends");
        ctx = getContext();
        maiil = MainActivity.mail.replace(".","_");

        noFriends = view.findViewById(R.id.no_friends_tv);
        noFriends.setVisibility(View.INVISIBLE);

        Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        recycler = (RecyclerView) getView().findViewById(R.id.friends_recycler);
        adapter = new FriendsAdapter(getContext(), items, getFragmentManager(),getActivity());

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        myFriendsRec = getView().findViewById(R.id.my_friedns_recycler);
        adapter2 = new MyFriendAdapter(ctx,myFriendsItems, getFragmentManager(),getActivity());
        myFriendsRec.setAdapter(adapter2);
        myFriendsRec.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("users/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data=dataSnapshot;
                myFriendsItems.clear();
                for (DataSnapshot ds: data.child(maiil+"/settings/friends").getChildren()){
                    Log.d("Friends",ds.getKey()+" "+ds.getValue().toString());
                    myFriendsItems.add(new FriendsItem(ds.getKey(), ds.getValue().toString()));

                }
                adapter2.notifyDataSetChanged();
                myFriendsRec.invalidate();
                if (myFriendsItems.size() == 0){
                    noFriends.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }
    public static void searchResult(String query){
        items.clear();

        if (data!=null){
            for (DataSnapshot ds: data.getChildren()){
                if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false")
                        && ds.hasChild("settings/nickname")
                        && ds.child("settings/nickname").getValue().toString().toLowerCase().contains(query.toLowerCase())
                        && !ds.getKey().toLowerCase().equals(maiil.toLowerCase())){

                    items.add(new FriendsItem(ds.getKey(),ds.child("settings/nickname").getValue().toString()));
                }

                else if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false")
                        && ds.hasChild("settings/nickname") && ds.child("settings/nickname").getValue().toString().toLowerCase().equals("")
                        && ds.getKey().split("@")[0].toLowerCase().contains(query.toLowerCase())
                        && !ds.getKey().toLowerCase().equals(maiil.toLowerCase())){

                    items.add(new FriendsItem(ds.getKey(),ds.getKey().split("@")[0]));
                }

            }
            adapter.notifyDataSetChanged();
            recycler.invalidate();
        }
    }
}
