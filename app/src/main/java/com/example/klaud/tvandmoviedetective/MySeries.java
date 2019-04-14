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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class MySeries extends Fragment {
    public static Context ctx;
    MySeriesAdapter adapter;
    RecyclerView recycler;
    ArrayList<SeriesItem> items = new ArrayList<>();

    public static Comparator<SeriesItem> compareSeriesItem() {
        Comparator comp = new Comparator<SeriesItem>() {
            @Override
            public int compare(SeriesItem si1, SeriesItem si2) {
                return si1.getName().compareTo(si2.getName());
            }
        };
        return comp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class", MainActivity.prefs.getString("class", ""));
        MainActivity.editor.putString("class", "MySeries");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.my_shows, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Series");
        ctx = getContext();

        recycler = view.findViewById(R.id.recycler_my_shows);
        adapter = new MySeriesAdapter(getContext(), items, getFragmentManager(), getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        MainActivity.viewPager.setVisibility(View.GONE);
        MainActivity.tabLayout.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail = MainActivity.prefs.getString("login", "").replace(".", "_");
        DatabaseReference dbRef = database.getReference("users/" + mail + "/series");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        registerForContextMenu(recycler);

    }

    private void showData(DataSnapshot snapshot) {
        items.clear();
        if (this.isVisible()) {
            SeriesItem si = null;
            for (DataSnapshot ds : snapshot.getChildren()) {
                TreeMap<String, Integer> seenEpisodes = new TreeMap<>();
                if (ds.hasChild("name") && ds.hasChild("networks") && ds.hasChild("poster_path")) {
                    si = new SeriesItem(ds.child("name").getValue().toString(), R.drawable.nopicture, Integer.decode(ds.getKey()));
                    si.setPoster_path(ds.child("poster_path").getValue().toString());
                    si.network = ds.child("networks").getValue().toString();
                } else {
                    //ds.getRef().removeValue();
                    Log.e("MySeriesErrror", "Series with incomplete information in Firebase");
                }

                for (DataSnapshot child : ds.getChildren()) {
                    if (child.getKey().contains("season")) {
                        String season = child.getKey().replace("season_", "");
                        if (season.length() < 2) season = "0" + season;
                        for (DataSnapshot child2 : child.getChildren()) {
                            String episode = child2.getKey();
                            if (episode.length() < 2) episode = "0" + episode;
                            seenEpisodes.put("S" + season + "E" + episode, Integer.decode(child2.getValue().toString()));
                        }
                    }
                }
                //Log.d("Episodes","----------------------------------------"+ds.getKey());
                //Log.d("Episodes",""+seenEpisodes);
                //Log.d("Episodes","----------------------------------------");
                if (!si.equals(null)) {
                    ArrayList<String> maxEpisode = new ArrayList<>(seenEpisodes.keySet());
                    if (maxEpisode.size() > 0) {
                        Collections.sort(maxEpisode);
                        si.lastSeen = maxEpisode.get(maxEpisode.size() - 1);
                    }
                    items.add(si);
                }

            }
            Collections.sort(items, compareSeriesItem());
            adapter.notifyDataSetChanged();
            recycler.invalidate();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int index = item.getOrder();
        SeriesItem series = items.get(index);
        switch (item.getItemId()) {
            case 1:
                String mail = MainActivity.mail.replace(".", "_");
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + mail + "/series/" + items.get(index).getId());
                dbRef.removeValue();
                items.remove(index);
                adapter.notifyDataSetChanged();
                recycler.invalidate();
                break;

            case 2:
                String pattern = "https://api.themoviedb.org/3/tv/%d/season/%d/episode/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
                String seasonAndEpLastSeen = series.lastSeen;
                if (seasonAndEpLastSeen == null) {
                    EpisodeFounder ef = new EpisodeFounder();
                    ef.execute(pattern, series.getId() + "", "1", "0");
                    //while (ef.getStatus() == AsyncTask.Status.RUNNING){}

                } else {
                    seasonAndEpLastSeen = seasonAndEpLastSeen.replace("S", "");
                    String[] numSeaAndEp = seasonAndEpLastSeen.split("E");

                    EpisodeFounder ef = new EpisodeFounder();
                    ef.execute(pattern, series.getId() + "", numSeaAndEp[0], numSeaAndEp[1]);
                    //while (ef.getStatus() == AsyncTask.Status.RUNNING){}

                }

                break;
        }
        return super.onContextItemSelected(item);
    }
}
