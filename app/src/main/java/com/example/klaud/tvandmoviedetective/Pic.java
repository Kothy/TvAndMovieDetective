package com.example.klaud.tvandmoviedetective;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public  class Pic extends AsyncTask<String, String, String> {
    Bitmap bitmap=null;
    Boolean series=false;
    @Override
    protected String doInBackground(String... params) {
        if (params.length>1) series=true;
        if (Looper.myLooper() == null) Looper.prepare();
        try {
            java.net.URL url = new java.net.URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(MovieDetail.ctx, "praca s obrazkom dokoncena++++++++++++", Toast.LENGTH_SHORT).show();
        return "done";
    }

    @Override
    protected void onPostExecute(String result) {
        if (series==false) MovieDetail.moviePoster.setImageBitmap(bitmap);
        else SeriesDetails.seriesPoster.setImageBitmap(bitmap);
    }

    @Override
    protected void onPreExecute() {  }
}

