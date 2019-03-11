package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Friends extends Fragment {
    public static Context ctx;
    public static TextView tv;
    static DataSnapshot data=null;
    public static RecyclerView recycler;
    public static FriendsAdapter adapter;
    public static ArrayList<FriendsItem> items = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","Friends");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.friends_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friends");
        ctx = getContext();
        tv = view.findViewById(R.id.friends_found);
        recycler = (RecyclerView) getView().findViewById(R.id.friends_recycler);
        adapter = new FriendsAdapter(getContext(), items,getFragmentManager(),getActivity());

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("users/");


        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data=dataSnapshot;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }
    public static void searchResult(String query){
        tv.setText("");
        items.clear();
        if (data!=null){
            for (DataSnapshot ds: data.getChildren()){
                /*if (ds.hasChild("settings/nickname") && !ds.child("settings/nickname").getValue().equals("")
                        && ds.child("settings/nickname").getValue().toString().toLowerCase().contains(query.toLowerCase())) {
                    if (ds.child("settings/private").getValue().toString().equals("false")){
                        tv.append(ds.child("settings/nickname").getValue().toString()+System.lineSeparator());
                    }

                }  else if (ds.child("settings/private").getValue().toString().equals("false")){
                    //items.add(new FriendsItem(ds.getKey(),ds.getKey().split("@")[0]));
                    tv.append(ds.getKey().split("@")[0]+System.lineSeparator());
                }*/
                if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false")
                        && ds.hasChild("settings/nickname") && ds.child("settings/nickname").getValue().toString().toLowerCase().contains(query.toLowerCase()) ){

                    items.add(new FriendsItem(ds.getKey(),ds.child("settings/nickname").getValue().toString()));
                }

                else if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false") &&
                        ds.hasChild("settings/nickname") && ds.child("settings/nickname").getValue().toString().toLowerCase().equals("") &&
                        ds.getKey().split("@")[0].toLowerCase().contains(query.toLowerCase())){
                    items.add(new FriendsItem(ds.getKey(),ds.getKey().split("@")[0]));
                }
            }
            adapter.notifyDataSetChanged();
            recycler.invalidate();
        }
    }
}
