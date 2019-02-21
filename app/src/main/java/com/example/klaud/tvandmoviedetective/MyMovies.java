package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyMovies extends Fragment {
    public Context ctx;
    public static ArrayList<MovieItem> items= new ArrayList<>();
    public static RecyclerView recycler;
    public static MovieAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","MyMovies");
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

    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        String ur="https://tvandmoviedetective.firebaseio.com/users/"+MainActivity.mail.replace(".","_")+"/movies.json";
        DataFromFirebase data=new DataFromFirebase();
        data.execute(ur,"1");
    }

}
