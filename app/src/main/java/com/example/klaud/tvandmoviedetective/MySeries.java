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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MySeries extends Fragment {
    MySeriesAdapter adapter;
    public static Context ctx;
    RecyclerView recycler;
    ArrayList<SeriesItem> items=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class",MainActivity.prefs.getString("class",""));
        MainActivity.editor.putString("class","MySeries");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.my_shows, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Series");
        ctx=getContext();

        recycler=view.findViewById(R.id.recycler_my_shows);
        adapter = new MySeriesAdapter(getContext(), items, getFragmentManager(),getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        MainActivity.viewPager.setVisibility(View.GONE);
        MainActivity.tabLayout.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail=MainActivity.mail.replace(".","_");
        DatabaseReference dbRef = database.getReference("users/"+mail+"/series");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    private void showData(DataSnapshot snapshot){
        items.clear();
        if (this.isVisible()){
            for (DataSnapshot ds: snapshot.getChildren()){
                TreeMap<String,Integer> seenEpisodes=new TreeMap<>();
                SeriesItem si=new SeriesItem(ds.child("name").getValue().toString(),R.drawable.a,Integer.decode(ds.getKey()));
                si.setPoster_path(ds.child("poster_path").getValue().toString());
                si.network=ds.child("networks").getValue().toString();

                for (DataSnapshot child:ds.getChildren()){
                    if (child.getKey().contains("season")){
                        String season=child.getKey().replace("season_","");
                        if (season.length()<2) season="0"+season;
                        for (DataSnapshot child2: child.getChildren()){
                            String episode=child2.getKey();
                            if (episode.length()<2) episode="0"+episode;
                            seenEpisodes.put("S"+season+"E"+episode,Integer.decode(child2.getValue().toString()));
                        }
                    }
                }
                Log.d("Episodes","----------------------------------------"+ds.getKey());
                Log.d("Episodes",""+seenEpisodes);
                Log.d("Episodes","----------------------------------------");
                ArrayList<String> maxEpisode=new ArrayList<>(seenEpisodes.keySet());
                if (maxEpisode.size() > 0){
                    Collections.sort(maxEpisode);
                    si.lastSeen=maxEpisode.get(maxEpisode.size()-1);
                }
                items.add(si);
            }
            adapter.notifyDataSetChanged();
            recycler.invalidate();
        }
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
    }
}
