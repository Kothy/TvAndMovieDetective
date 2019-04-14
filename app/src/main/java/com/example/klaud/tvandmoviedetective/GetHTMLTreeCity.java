package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class GetHTMLTreeCity extends AsyncTask<String, Integer, String> {
    @Override
    protected void onPreExecute() {
        if (Looper.myLooper() == null) Looper.prepare();
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            Document doc = Jsoup.connect(params[0])
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();

            Theatres.theatres.clear();
            Theatres.urlForTheatres.clear();
            Theatres.theatres.add("Choose theatre");
            Theatres.urlForTheatres.add("empty-link");
            Element body = doc.body();
            for (Element e : body.getElementsByTag("select")) {
                if (e.attributes().hasKey("id") && e.attr("id").contains("cinema-place-select")) {
                    for (Element ee : e.getElementsByTag("option")) {
                        if (!ee.text().equals("Všetky kiná")) {
                            Log.d("TREE", ee.text());
                            Theatres.theatres.add(ee.text());
                            Theatres.urlForTheatres.add(ee.attr("data-link"));
                        }
                    }
                }
            }
            Log.d("TREE", "-----------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(String result) {
        Theatres.adapter2.notifyDataSetChanged();
        Theatres.spinnerTheatres.invalidate();
    }

}
