package com.example.klaud.tvandmoviedetective;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FillInfoForSearchMovies extends AsyncTask<String, String, String> {
    ProgressDialog pd;

    @Override
    protected String doInBackground(String... strings) {
        try (BufferedReader br = new BufferedReader(new FileReader(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/Detective/" + "movie_ids_" + strings[1] + ".json"))) {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    line = line.toLowerCase();
                    MainActivity.movies.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings[0];
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(MainActivity.ctx, "Skoncil som s citanim", Toast.LENGTH_SHORT).show();
        pd.dismiss();
        try {
            MoviesResultSearch.doMySearch(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(MainActivity.ctx, "Zacinam citat", Toast.LENGTH_SHORT).show();
        pd = new ProgressDialog(MoviesResultSearch.ctx);
        pd.setTitle("Please wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.setMax(100);
        pd.show();
    }
}
