package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

public class MyShows extends Fragment {
    MySeriesAdapter adapter;
    public static Context ctx;
    RecyclerView recycler;
    ArrayList<SeriesItem> items=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","MySeries");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.my_shows, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Series");
        ctx=getContext();
        items.add(new SeriesItem("one",R.drawable.a,55));
        items.add(new SeriesItem("two",R.drawable.bear,55));
        items.add(new SeriesItem("three",R.drawable.a,55));
        items.add(new SeriesItem("four",R.drawable.bear,55));
        items.add(new SeriesItem("four",R.drawable.bear,55));
        items.add(new SeriesItem("four",R.drawable.bear,55));
        items.add(new SeriesItem("four",R.drawable.bear,55));
        items.add(new SeriesItem("four",R.drawable.bear,55));

        recycler=view.findViewById(R.id.recycler_my_shows);
        adapter = new MySeriesAdapter(getContext(), items, getFragmentManager(),getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        MainActivity.viewPager.setVisibility(View.GONE);
        MainActivity.tabLayout.setVisibility(View.GONE);

    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }


    }

}
