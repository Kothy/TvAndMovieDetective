package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class GetHTMLTreeProgram extends AsyncTask<String, Integer, String> {
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

            Element body = doc.body();
            Theatres.items.clear();
            String date = "";
            //Theatres.noProgram.setVisibility(View.INVISIBLE);
            for (Element e : body.getElementsByTag("div")) {
                if (e.hasClass("place-premieres-head")) {
                    String[] arr = e.text().split("-");
                    date = arr[arr.length - 1].replace(" ", "").replace("skryťprogram", "").replace("zobraziťprogram", "");
                }
                if (e.attributes().hasKey("id") && e.attributes().get("id").contains("premieres-board_")) {
                    Log.d("RESULT", "****************************" + System.lineSeparator());
                    String titl = "", pg = "";
                    for (Element e2 : e.getElementsByTag("tr")) {
                        if (e2.attributes().hasKey("class") && e2.attributes().get("class").equals("board-row")) {
                            for (Element title : e2.getElementsByTag("a")) {
                                titl = title.text();
                                Log.d("RESULT", title.text());
                            }
                            String times = "";
                            for (Element time : e2.getElementsByTag("span")) {
                                if (time.attributes().hasKey("class") && time.attr("class").contains("time")) {
                                    Log.d("RESULT", time.text());
                                    times += time.text() + "  |  ";
                                }

                            }
                            times = times.substring(0, times.length() - 5);
                            String len = "", dabb = "", age = "";
                            for (Element el : e2.getElementsByTag("td")) {
                                if (el.hasAttr("class")) {
                                    if (el.attr("class").equals("col-length")) len = el.text();
                                    else if (el.attr("class").equals("col-lang")) dabb = el.text();
                                    else if (el.attr("class").equals("col-age")) age = el.text();
                                }
                            }
                            String len_plus_dab = len + " | " + dabb;
                            if (age.equals("U")) age = "Suitable for all";
                            if (dabb.equals("-") || dabb.equals("") || dabb.length() < 2)
                                dabb = "Dabbing";
                            if (len.equals("-")) {
                                len_plus_dab = dabb;
                            }
                            if (len_plus_dab.charAt(len_plus_dab.length() - 1) == ' ')
                                len_plus_dab += "Dabbing";
                            Theatres.items.add(new TheatresItem(titl, age, len_plus_dab, times, date.toString()));
                            //Theatres.data.add(times);
                            //Theatres.data.add("**************************************");
                        }
                    }
                    //Log.d("RESULT","--------------------------------------"+System.lineSeparator());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(String result) {
        if (Theatres.items.size() == 0) {
            //Toast.makeText(MainActivity.ctx, "Selected cinema have no program for next days", Toast.LENGTH_SHORT).show();
            Theatres.noProgram.setText("Selected cinema have no program for next days.");
            Theatres.noProgram.setVisibility(View.VISIBLE);

        } else Theatres.noProgram.setVisibility(View.INVISIBLE);
        Theatres.rec_adapter.notifyDataSetChanged();
        Theatres.recycler.invalidate();
    }
}
