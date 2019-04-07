package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyMoviesWatched extends Fragment {
    public Context ctx;
    public static ArrayList<MovieItem> items= new ArrayList<>();
    public static RecyclerView recycler;
    public static MyMoviesAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class",MainActivity.prefs.getString("class",""));
        MainActivity.editor.putString("class","MyMoviesWatched");
        MainActivity.editor.apply();
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.viewPager.setVisibility(View.VISIBLE);
        MainActivity.tabLayout.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.my_movies, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My movies");
        ctx=getContext();
        recycler = (RecyclerView) getView().findViewById(R.id.recyclerVert);
        recycler.setVisibility(View.VISIBLE);
        adapter = new MyMoviesAdapter(getContext(), items,getFragmentManager(),getActivity());

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(view.getContext(),3));

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail=MainActivity.mail.replace(".","_");
        DatabaseReference dbRef = database.getReference("users/"+mail+"/movies");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                Boolean delete=false;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.hasChild("status")){
                        if ( ds.child("status").getValue().toString().equals("watched")){
                            MovieItem mi=new MovieItem(ds.child("title").getValue().toString(), R.drawable.nopicture, Integer.decode(ds.getKey()));
                            if (ds.hasChild("poster_path")){
                                if ( !ds.child("poster_path").getValue().toString().equals("null")){
                                    mi.setPoster_path(ds.child("poster_path").getValue().toString());
                                }
                                items.add(mi);
                            } else delete = true;
                        }
                    } else delete=true;
                    if (delete) {
                        DatabaseReference dbRef = database.getReference("users/"+mail+"/movies"+ds.getKey());
                        dbRef.removeValue();
                    }
                }
                Collections.sort(items, compareMovieItems());
                adapter.notifyDataSetChanged();
                recycler.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
    }

    public Comparator<MovieItem> compareMovieItems() {
        Comparator comp = new Comparator<MovieItem>(){
            @Override
            public int compare(MovieItem mi1, MovieItem mi2) {
                return mi1.getName().compareTo(mi2.getName());
            }
        };
        return comp;
    }
}
