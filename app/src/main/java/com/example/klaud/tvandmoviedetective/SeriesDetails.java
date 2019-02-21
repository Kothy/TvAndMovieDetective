package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeriesDetails extends Fragment {
    TextView tv;
    ScrollView sv;
    Integer Id=0;
    ListView castLv;
    Integer numOfSeasons=0;
    JSONObject jsonSeries =null;
    ArrayCast castAndCrew=null;
    public static ImageView seriesPoster;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> pairs = new ArrayList<Map<String, String>>();
    public static Context ctx;
    Button episodesButt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","SeriesDetail");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.serie_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Series Details");
        ctx=getContext();
        tv = view.findViewById(R.id.textView40);
        castLv = view.findViewById(R.id.listVCast5);
        castLv.setFocusable(false);
        sv = view.findViewById(R.id.scrollView20);
        sv.setOnTouchListener((vie,event) -> {
            tv.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        castLv.setOnTouchListener((vie,event) -> {
            castLv.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        pairs.clear();
        seriesPoster =view.findViewById(R.id.imageView20);
        String[] from = {"actor", "role"};// symbolické mená riadkov
        int[] to = { android.R.id.text1, android.R.id.text2 };
        adapter=new SimpleAdapter(getContext(), pairs,android.R.layout.simple_list_item_2, from, to);
        castLv.setAdapter(adapter);
        episodesButt = view.findViewById(R.id.button6);
        episodesButt.setOnClickListener(click ->{
            Fragment fragment = null;
            fragment = new Episodes();
            Bundle bundle = new Bundle();
            bundle.putString("id", Id+"");
            bundle.putString("title",getActivity().getTitle()+"");
            bundle.putInt("seasons",numOfSeasons);

            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String movieID = bundle.getString("id", "");
            Id=Integer.valueOf(movieID);
            String pattern="https://api.themoviedb.org/3/tv/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&append_to_response=season/1,season/2";
            String pattern2="https://api.themoviedb.org/3/tv/%d/credits?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            getJsonString.execute(String.format(pattern, Id));
            getJsonCast.execute(String.format(pattern2, Id));
        }
    }

    AsyncTask<String, Integer, String> getJsonString = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
        }
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            try {
                jsonSeries =new JSONObject(result);
                String patt="https://image.tmdb.org/t/p/w500%s";
                if (jsonSeries.getString("overview").equals("")) tv.setText("Overview is not available.");
                else tv.setText(jsonSeries.getString("overview"));
                getActivity().setTitle(jsonSeries.getString("name"));
                Pic image=new Pic();
                numOfSeasons=jsonSeries.getJSONArray("seasons").length();
                //Toast.makeText(ctx, numOfSeasons+" num of seasons", Toast.LENGTH_SHORT).show();
                if (jsonSeries.getString("backdrop_path").equals("null")){
                    seriesPoster.setBackgroundResource(R.drawable.no_backdrop);
                } else image.execute(String.format(patt, jsonSeries.getString("backdrop_path")),"series");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    AsyncTask<String, Integer, String> getJsonCast = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
        }
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result){
            pairs.clear();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            castAndCrew=gson.fromJson(result, ArrayCast.class);
            for (Cast c:castAndCrew.cast) {
                if (!c.character.contains("uncredited") && !c.character.contains("On-Set")){
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("actor", c.name);
                    item.put("role", c.character);
                    pairs.add(item);
                }
            }
            for (Crew c:castAndCrew.crew) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("actor", c.name);
                item.put("role", c.job);
                pairs.add(item);
            }
            adapter.notifyDataSetChanged();
            castLv.invalidate();
        }
    };
}
