package com.example.klaud.tvandmoviedetective;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MoviesResultSearch extends Fragment {
    public static List<HashMap<String, String>> aList= Collections.synchronizedList(new ArrayList());
    public static ArrayList<JSONObject> mov=new ArrayList<>();
    public static ArrayList<JSONObject> movTrend=new ArrayList<>();
    public static ArrayList<MovieItem> itemsTrending = new ArrayList<>();
    public static ArrayList<MovieItem> searchedItems = new ArrayList<>();
    private static ArrayList<MovieItem> itemsInTheatres = new ArrayList<>();
    public static ArrayList<String> postersOfSearch=new ArrayList<>();
    public static SimpleAdapter simpleAdapter;
    public static ListView lv;
    ProgressDialog pd;
    public static Context ctx;
    public static RecyclerView recycler, recycler2, recycler3;
    public static MovieAdapter adapter, adapter2;
    public static ResultSearchAdapter adapter3;
    TextView trendingTitle;
    TextView theatresTitle;
    public static View view;
    public static FragmentManager fm;
    public static Activity actvity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","Movie");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.movies_search_result_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.prefs.getString("search","").equals("")){
            getActivity().setTitle("Movies");
        } else getActivity().setTitle("");
        this.view=view;
        this.fm=getFragmentManager();
        ctx=getContext();
        actvity=getActivity();
        trendingTitle= view.findViewById(R.id.textView3);
        theatresTitle = view.findViewById(R.id.textView);
        theatresTitle.setVisibility(View.INVISIBLE);
        trendingTitle.setVisibility(View.INVISIBLE);

        recycler = (RecyclerView) getView().findViewById(R.id.recycler);
        adapter = new MovieAdapter(getContext(), itemsTrending,getFragmentManager(),getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler2 = (RecyclerView) getView().findViewById(R.id.recycler2);
        adapter2 = new MovieAdapter(getContext(), itemsInTheatres, getFragmentManager(),getActivity());
        recycler2.setAdapter(adapter2);
        recycler2.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler3 = (RecyclerView) getView().findViewById(R.id.searchRecycler);
        adapter3 = new ResultSearchAdapter(getContext(), searchedItems, getFragmentManager(),getActivity());
        recycler3.setAdapter(adapter3);
        recycler3.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        pd=new ProgressDialog(getView().getContext());
        pd.setTitle("Loading Data");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.setMax(100);

        String[] from = {"listview_image", "listview_title", "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
        lv = (ListView) getView().findViewById(R.id.list);

        simpleAdapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_activity, from, to);
        lv.setAdapter(simpleAdapter);
        if (!MainActivity.prefs.getString("search","").equals("")){
            //lv.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.INVISIBLE);
            recycler2.setVisibility(View.INVISIBLE);
            recycler3.setVisibility(View.VISIBLE);
            theatresTitle.setVisibility(View.INVISIBLE);
            trendingTitle.setVisibility(View.INVISIBLE);
            try {
                doMySearch(MainActivity.prefs.getString("search",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainActivity.editor.putString("search","");
            MainActivity.editor.apply();

        } else {
            lv.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.VISIBLE);
            recycler2.setVisibility(View.VISIBLE);
            recycler3.setVisibility(View.INVISIBLE);
            theatresTitle.setVisibility(View.VISIBLE);
            trendingTitle.setVisibility(View.VISIBLE);
            trendingTitle.setText("Trending now");
            theatresTitle.setText("In theaters");
            if (mov.size()>0) displayList();
            else getJsonInTheatres.execute();

            if (movTrend.size()>0) displayList2();
            else getJsonTrending.execute();

            //Toast.makeText(ctx, "mala by som zobrazit aktualne filmy", Toast.LENGTH_SHORT).show();
        }

        lv.setOnItemClickListener((AdapterView<?> adapt, View viev, int pos, long arg3)->{
            //Toast.makeText(getContext(), ""+pos, Toast.LENGTH_LONG).show();
            Fragment fragment = null;
            fragment = new MovieDetail();
            Bundle bundle = new Bundle();
            bundle.putString("id", aList.get(pos).get("id"));
            bundle.putString("title", aList.get(pos).get("listview_title"));
            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });

    }

    public void displayList(){
        itemsTrending.clear();
        adapter = new MovieAdapter(getContext(), itemsTrending, getFragmentManager(),getActivity());
        recycler.setAdapter(adapter);
        try {
            for (JSONObject json: mov) {
                MovieItem mi=new MovieItem(json.getString("original_title"),R.drawable.a,json.getInt("id"));
                mi.setPoster_path(json.getString("poster_path"));
                itemsTrending.add(mi);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        recycler.invalidate();
    }
    public void displayList2(){
        itemsInTheatres.clear();
        try {
            for (JSONObject json: movTrend) {
                MovieItem mi=new MovieItem(json.getString("original_title"),R.drawable.a,json.getInt("id"));
                mi.setPoster_path(json.getString("poster_path"));
                itemsInTheatres.add(mi);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter2.notifyDataSetChanged();
        recycler2.invalidate();
    }
    public static void doMySearch(String query) throws JSONException {// funguje ale je to strasne pomale
        ArrayList<JSONObject> found = new ArrayList<>();
        query=query.toLowerCase();
        for (int i=0; i<MainActivity.movies.size();i++) {
            String text=MainActivity.movies.get(i);
            if (text!=null && text.contains(query)){

                JSONObject js=new JSONObject(text);
                Log.d("Hladanie",text+"");
                if (js.getBoolean("adult")==false){
                    found.add(js);
                    postersOfSearch.add(null);
                }
            }
        }
        searchedItems.clear();
        Collections.sort(found, compareJSONObject());
        lv.setVisibility(View.INVISIBLE);
        for (JSONObject js : found) {
            MovieItem mi=new MovieItem(js.getString("original_title"),R.drawable.a,js.getInt("id"));
            mi.setPoster_path("null");
            searchedItems.add(mi);
            DetailsForSearch ds=new DetailsForSearch();
            String patt="https://api.themoviedb.org/3/movie/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            int pos=searchedItems.size()-1;
            if (pos==-1) pos=0;
            ds.execute(String.format(patt,js.getInt("id")),pos+"");

        }
        adapter3.notifyDataSetChanged();
        recycler3.invalidate();

    }
    public String randomString() {
        byte[] array = new byte[15];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
    public static Comparator<JSONObject> compareJSONObject() {
        Comparator comp = new Comparator<JSONObject>(){
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    Double d1=o1.getDouble("popularity");
                    Double d2=o2.getDouble("popularity");
                    return d2.compareTo(d1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        return comp;
    }
    AsyncTask<String, Integer, String> getJsonInTheatres = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
            pd.show();
        }
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            pd.show();
            try {
                URL myUrl = new URL("https://api.themoviedb.org/3/movie/now_playing?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&page=1");
                //"https://api.themoviedb.org/3/movie/now_playing?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&page=1"
                //https://api.themoviedb.org/3/trending/movie/day?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4
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
            //tv.setText(result);
            try {
                JSONObject js=new JSONObject(result);
                JSONArray arr=js.getJSONArray("results");
                for (int i = 0; i <arr.length() ; i++) {
                    mov.add(arr.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            displayList();
        }
    };
    AsyncTask<String, Integer, String> getJsonTrending = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
            pd.show();
        }
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            try {
                URL myUrl = new URL("https://api.themoviedb.org/3/trending/movie/day?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4");
                //URL myUrl = new URL("https://api.themoviedb.org/3/trending/movie/day?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4");
                //https://api.themoviedb.org/3/movie/335983?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US
                //https://api.themoviedb.org/3/trending/movie/day?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4
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
            //tv.setText(result);
            try {
                JSONObject js=new JSONObject(result);
                JSONArray arr=js.getJSONArray("results");
                for (int i = 0; i <arr.length() ; i++) {
                    movTrend.add(arr.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            displayList2();
        }
    };
}
