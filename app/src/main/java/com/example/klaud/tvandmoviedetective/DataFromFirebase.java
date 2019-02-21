package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class DataFromFirebase extends AsyncTask<String, String, String> {
    String pos;
    @Override
    protected String doInBackground(String... strings) {
        String result;
        String inputLine;
        pos=strings[1];
        try {
            URL myUrl = new URL(strings[0]);
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
    @Override
    protected void onPostExecute(String result){
        try {

            JSONObject js=new JSONObject(result);
            Iterator<String> keys = js.keys();
            ArrayList<MovieItem> movies=null;
            RecyclerView rec=null;
            MovieAdapter ad=null;
            if (pos.equals("1")) {
                ad= MyMovies.adapter;
                rec= MyMovies.recycler;
                movies= MyMovies.items;
            }
            else if (pos.equals("2")) {
                ad= MyMoviesWatched.adapter;
                rec=MyMoviesWatched.recycler;
                movies=MyMoviesWatched.items;
            }
            movies.clear();
            while(keys.hasNext()) {
                String key = keys.next();
                if (js.get(key) instanceof JSONObject) {
                    JSONObject jsonik=js.getJSONObject(key);
                    if (jsonik.getString("status").equals("want") && pos.equals("1"))
                        movies.add(new MovieItem(jsonik.getString("title"), R.drawable.a,Integer.parseInt(key)));
                    else if (jsonik.getString("status").equals("watched") && pos.equals("2"))
                        movies.add(new MovieItem(jsonik.getString("title"),R.drawable.a,Integer.parseInt(key)));
                }
            }
            ad.notifyDataSetChanged();
            rec.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
    }
}