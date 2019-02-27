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
                    MoviesResultSearch.searchedItems.get(position).release_date=json.getString("release_date");
                    //MoviesResultSearch.adapter3 = new ResultSearchAdapter(MoviesResultSearch.ctx, MoviesResultSearch.searchedItems, MoviesResultSearch.fm, MoviesResultSearch.actvity);
                    //MoviesResultSearch.recycler3.setAdapter(MoviesResultSearch.adapter3);
                    MoviesResultSearch.adapter3.notifyItemChanged(position);
                    MoviesResultSearch.recycler3.invalidateItemDecorations();
                    MoviesResultSearch.recycler3.invalidate();
                    Log.d("Dokoncenie","dokoncil som pracu");
                } else {
                    TvSeriesResultSearch.searchedItems.get(position).setPoster_path(json.getString("poster_path"));
                    TvSeriesResultSearch.searchedItems.get(position).release_date = json.getString("first_air_date");
                    //TvSeriesResultSearch.adapter3 = new ResultSearchAdapterSeries(TvSeriesResultSearch.ctx, TvSeriesResultSearch.searchedItems, TvSeriesResultSearch.fm, TvSeriesResultSearch.activity);
                    //TvSeriesResultSearch.recycler3.setAdapter(TvSeriesResultSearch.adapter3);
                    TvSeriesResultSearch.adapter3.notifyItemChanged(position);
                    TvSeriesResultSearch.recycler3.invalidateItemDecorations();
                    TvSeriesResultSearch.recycler3.invalidate();
                    Log.d("Dokoncenie","dokoncil som pracu");
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {  }
}
