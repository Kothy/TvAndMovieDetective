package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailsForSearch extends AsyncTask<String, String, String> {
    Integer position;
    @Override
    protected String doInBackground(String... strings) {
        String result;
        String inputLine;
        position=Integer.decode(strings[1]);
        try {
            URL myUrl = new URL(strings[0]);
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

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result!= null){
                JSONObject json=new JSONObject(result);
                MoviesResultSearch.searchedItems.get(position).setPoster_path(json.getString("poster_path"));
                MoviesResultSearch.searchedItems.get(position).release_date=json.getString("release_date");
                MoviesResultSearch.adapter3 = new ResultSearchAdapter(MoviesResultSearch.ctx, MoviesResultSearch.searchedItems, MoviesResultSearch.fm, MoviesResultSearch.actvity);
                MoviesResultSearch.recycler3.setAdapter(MoviesResultSearch.adapter3);
                Log.d("Dokoncenie","dokoncil som pracu");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {  }
}
