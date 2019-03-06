package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

public class GetHTMLTreeProgram  extends AsyncTask<String, Integer,String> {
    @Override
    protected void onPreExecute() {
        if (Looper.myLooper() == null) Looper.prepare();
    }
    @Override
    protected String doInBackground(String... params){
        String result="";
        try {
            Document doc = Jsoup.connect(params[0])
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();
            Element body=doc.body();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(ts);
            Theatres.data.clear();
            for (Element e: body.getElementsByTag("div")){

                if (e.attributes().hasKey("id") && e.attributes().get("id").contains("premieres-board_")) {
                    ts.setTime(cal.getTime().getTime());
                    Theatres.data.add("************"+cal.getTime().getDate()+"."+cal.getTime().getMonth()+"."+cal.getTime().getYear()+"****************");
                    Log.d("RESULT","************"+cal.getTime().getDate()+"."+cal.getTime().getMonth()+"."+cal.getTime().getYear()+"****************"+System.lineSeparator());
                    for (Element e2:e.getElementsByTag("tr")){
                        if (e2.attributes().hasKey("class") && e2.attributes().get("class").equals("board-row")){
                            for (Element title: e2.getElementsByTag("a")){
                                Theatres.data.add(title.text());
                                Log.d("RESULT",title.text());
                            }
                            String times="";
                            for (Element time: e2.getElementsByTag("span")){
                                if (time.attributes().hasKey("class") && time.attr("class").contains("time")){
                                    Log.d("RESULT",time.text());
                                    //Theatres.data.add(time.text());
                                    times+=time.text()+"  |  ";
                                }

                            }
                            Theatres.data.add(times);
                            Theatres.data.add("**************************************");
                        }
                    }
                    //Log.d("RESULT","--------------------------------------"+System.lineSeparator());
                    cal.add(Calendar.DAY_OF_WEEK,1);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(String result){
        Theatres.adapter3.notifyDataSetChanged();
        Theatres.lv.invalidate();
    }
}
