package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProfile extends Fragment {
    Context ctx;
    String nick, email;
    ArrayList<MovieItem> watchedItems=new ArrayList<>();
    ArrayList<MovieItem> wantItems=new ArrayList<>();
    ArrayList<SeriesItem> seriesItems=new ArrayList<>();
    public static RecyclerView recycler,recycler2,recycler3;
    public static MyMoviesAdapter adapter, adapter2;
    private static SeriesAdapter adapter3;
    ScrollView scrollView;
    TextView tvMoviesWatched,tvMoviesWant,tvSeries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","User profile");
        MainActivity.editor.apply();

        return inflater.inflate(R.layout.user_profile, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx=getContext();

        tvMoviesWant = view.findViewById(R.id.users_movies_want);
        tvMoviesWatched = view.findViewById(R.id.users_movies_watched);
        tvSeries = view.findViewById(R.id.users_series_tv);

        tvMoviesWant.setVisibility(View.VISIBLE);
        tvMoviesWatched.setVisibility(View.VISIBLE);
        tvSeries.setVisibility(View.VISIBLE);

        recycler = (RecyclerView) getView().findViewById(R.id.users_want_movies);
        adapter = new MyMoviesAdapter(getContext(), wantItems, getFragmentManager(), getActivity(), true);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler2 = (RecyclerView) getView().findViewById(R.id.users_watched_movies);
        adapter2 = new MyMoviesAdapter(getContext(), watchedItems, getFragmentManager(), getActivity(), true);
        recycler2.setAdapter(adapter2);
        recycler2.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler3 = (RecyclerView) getView().findViewById(R.id.users_series);
        adapter3 = new SeriesAdapter(getContext(), seriesItems,getFragmentManager(),getActivity(),true);
        recycler3.setAdapter(adapter3);
        recycler3.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        SeriesItem sm=new SeriesItem("harry",R.drawable.nopicture,0);
        sm.setPoster_path("null");

        seriesItems.add(sm);

        scrollView = view.findViewById(R.id.users_scroll);


    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nick = bundle.getString("nickname");
            email = bundle.getString("email");
            getActivity().setTitle(nick+"'s profile");

            tvSeries.setText(nick+"'s series");
            tvMoviesWatched.setText(nick+"'s movies watched");
            tvMoviesWant.setText(nick+"'s movies want to watch");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = database.getReference("users/"+email);

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    watchedItems.clear();
                    wantItems.clear();
                    if (dataSnapshot.hasChild("movies")){
                        for (DataSnapshot ds: dataSnapshot.child("movies").getChildren()){
                            if (ds.child("status").getValue().toString().equals("watched")){
                                MovieItem mi=new MovieItem(ds.child("title").getValue().toString(), R.drawable.nopicture, Integer.decode(ds.getKey()));
                                //MovieItem mi=new MovieItem("Watched", R.drawable.nopicture, Integer.decode(ds.getKey()));
                                if (! ds.child("poster_path").getValue().toString().equals("null")){
                                    mi.setPoster_path(ds.child("poster_path").getValue().toString());
                                }
                                watchedItems.add(mi);
                            }
                            else if (ds.child("status").getValue().toString().equals("want")){
                                MovieItem mi=new MovieItem(ds.child("title").getValue().toString(), R.drawable.nopicture, Integer.decode(ds.getKey()));
                                //MovieItem mi=new MovieItem("Want", R.drawable.nopicture, Integer.decode(ds.getKey()));
                                if (! ds.child("poster_path").getValue().toString().equals("null")){
                                    mi.setPoster_path(ds.child("poster_path").getValue().toString());
                                }
                                wantItems.add(mi);

                            }
                        }
                        if (wantItems.size()==0){
                            tvMoviesWant.setVisibility(View.GONE);
                        }
                        if (watchedItems.size()==0){
                            tvMoviesWatched.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                        recycler.invalidate();
                        adapter2.notifyDataSetChanged();
                        recycler2.invalidate();
                    }
                    if (dataSnapshot.hasChild("series")){
                        Toast.makeText(ctx, "naslo sa series", Toast.LENGTH_SHORT).show();
                        seriesItems.clear();
                        for (DataSnapshot ds: dataSnapshot.child("series").getChildren()){
                            SeriesItem si=new SeriesItem(ds.child("name").getValue().toString(), R.drawable.nopicture, Integer.decode(ds.getKey()));
                            si.setPoster_path(ds.child("poster_path").getValue().toString());
                            seriesItems.add(si);

                        }
                        if (seriesItems.size()==0){
                            tvSeries.setVisibility(View.GONE);
                        }
                        adapter3.notifyDataSetChanged();
                        recycler3.invalidate();
                    }
                    if (watchedItems.size()==0 && wantItems.size()==0 && seriesItems.size()==0){
                        tvMoviesWant.setVisibility(View.GONE);
                        tvMoviesWant.setText("User have nothing to show");
                        tvSeries.setVisibility(View.GONE);
                        tvMoviesWatched.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


        }
    }
    @Override
    public void onPause(){
        super.onPause();
    }

}
