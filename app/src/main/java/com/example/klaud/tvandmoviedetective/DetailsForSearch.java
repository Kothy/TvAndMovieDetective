package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
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
    Boolean series=false;
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
        if (strings.length==3) series=true;
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result!= null){
                JSONObject json=new JSONObject(result);
                if (series==false){
                    MoviesResultSearch.searchedItems.get(position).setPoster_path(json.getString("poster_path"));
                    if (json.getString("release_date").length() > 5) MoviesResultSearch.searchedItems.get(position).release_date=json.getString("release_date").substring(0,4);
                    MoviesResultSearch.adapter3.notifyItemChanged(position);
                    MoviesResultSearch.recycler3.invalidateItemDecorations();
                    MoviesResultSearch.recycler3.invalidate();

                } else {
                    TvSeriesResultSearch.searchedItems.get(position).setPoster_path(json.getString("poster_path"));
                    if (json.getString("first_air_date").length() > 5) TvSeriesResultSearch.searchedItems.get(position).release_date = json.getString("first_air_date").substring(0,4);
                    TvSeriesResultSearch.adapter3.notifyItemChanged(position);
                    TvSeriesResultSearch.recycler3.invalidateItemDecorations();
                    TvSeriesResultSearch.recycler3.invalidate();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {  }
}
