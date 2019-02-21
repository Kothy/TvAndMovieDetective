package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyMoviesWatched extends Fragment {
    public Context ctx;
    public static ArrayList<MovieItem> items= new ArrayList<>();
    public static RecyclerView recycler;
    public static MovieAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        adapter = new MovieAdapter(getContext(), items,getFragmentManager(),getActivity());

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(view.getContext(),3));
        /*items.add(new MovieItem("film1",R.drawable.a,555));
        items.add(new MovieItem("film2",R.drawable.a,555));
        items.add(new MovieItem("film3",R.drawable.a,555));

        adapter.notifyDataSetChanged();
        recycler.invalidate();*/
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        String ur="https://tvandmoviedetective.firebaseio.com/users/"+MainActivity.mail.replace(".","_")+"/movies.json";
        DataFromFirebase data=new DataFromFirebase();
        data.execute(ur,"2");
    }
}
