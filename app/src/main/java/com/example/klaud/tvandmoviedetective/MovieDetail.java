package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class MovieDetail  extends Fragment {
    TextView tv;
    ScrollView sv;
    Integer movieId=0;
    String title="";
    ListView castLv;
    JSONObject jsonMovie=null;
    ArrayCast castAndCrew=null;
    public static ImageView moviePoster;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> pairs = new ArrayList<Map<String, String>>();
    public static Context ctx;
    Button watchedButton, wantToWatchButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","MovieDetail");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.movie_detail, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Movie Details");
        ctx=getContext();
        tv = view.findViewById(R.id.textView4);
        castLv = view.findViewById(R.id.listVCast);
        castLv.setFocusable(false);
        sv = view.findViewById(R.id.scrollView2);
        watchedButton = view.findViewById(R.id.button3);
        wantToWatchButton = view.findViewById(R.id.button4);
        if (MyMovies.recycler!= null) {
            MyMovies.recycler.setVisibility(View.INVISIBLE);
        }
        if (MyMoviesWatched.recycler!= null) {
            MyMoviesWatched.recycler.setVisibility(View.INVISIBLE);
        }
        if (MainActivity.appbar !=null){
            MainActivity.appbar.setVisibility(View.INVISIBLE);
        }
        String maiil=MainActivity.mail.replace(".","_");
        watchedButton.setOnClickListener((click) -> {

            SaveToFirebase save=new SaveToFirebase();
            save.execute("/users/"+maiil+"/movies/"+movieId,"watched",title);
        });
        wantToWatchButton.setOnClickListener((click) -> {

            SaveToFirebase save=new SaveToFirebase();
            save.execute("/users/"+maiil+"/movies/"+movieId,"want",title);
        });
        sv.setOnTouchListener((vie,event) -> {
            tv.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        castLv.setOnTouchListener((vie,event) -> {
            castLv.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        pairs.clear();
        moviePoster=view.findViewById(R.id.imageView3);
        String[] from = {"actor", "role"};// symbolické mená riadkov
        int[] to = { android.R.id.text1, android.R.id.text2 };
        adapter=new SimpleAdapter(getContext(), pairs,android.R.layout.simple_list_item_2, from, to);
        castLv.setAdapter(adapter);
        MainActivity.viewPager.setVisibility(View.GONE);
        MainActivity.tabLayout.setVisibility(View.GONE);

    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String movieID = bundle.getString("id", "");
            title=bundle.getString("title", "");
            movieId=Integer.valueOf(movieID);
            String pattern="https://api.themoviedb.org/3/movie/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            String pattern2="https://api.themoviedb.org/3/movie/%d/credits?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            getJsonString.execute(String.format(pattern, movieId));
            getJsonCast.execute(String.format(pattern2, movieId));
        }
        //Toast.makeText(getContext(), "Idem zobrazovat film s id: "+movieId, Toast.LENGTH_LONG).show();

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
                jsonMovie=new JSONObject(result);
                String patt="https://image.tmdb.org/t/p/original%s";
                if (jsonMovie.getString("overview").equals("")) tv.setText("Overview is not available.");
                else tv.setText(jsonMovie.getString("overview"));
                getActivity().setTitle(jsonMovie.getString("original_title"));
                Pic image=new Pic();


                if (jsonMovie.getString("backdrop_path").equals("null")){
                    moviePoster.setBackgroundResource(R.drawable.no_backdrop);
                } else image.execute(String.format(patt,jsonMovie.getString("backdrop_path")));

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
                if (c.job.equals("Director") || c.job.equals("Screenplay")){
                    item.put("actor", c.name);
                    item.put("role", c.job);
                    pairs.add(item);
                }
            }

            adapter.notifyDataSetChanged();
            castLv.invalidate();
            //Toast.makeText(getContext(), "dokoncil som pracu s jsonom: ", Toast.LENGTH_SHORT).show();
        }
    };
}

