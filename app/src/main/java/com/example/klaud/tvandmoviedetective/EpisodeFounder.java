package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EpisodeFounder extends AsyncTask<String, String, String> {
    JSONObject epis = null;
    String res = null;
    String pattern = "";
    String epCode = "";

    int s = 1, e = 1, id = 0;

    @Override
    protected String doInBackground(String... strings) {
        String result = null;

        pattern = strings[0];
        id = Integer.valueOf(strings[1]);
        s = Integer.valueOf(strings[2]);
        e = Integer.valueOf(strings[3]);

        Log.d("URL", String.format(pattern, id, s, e + 1));
        backgroundJob(String.format(pattern, id, s, e + 1));
        String mail = MainActivity.mail.replace(".", "_");
        if (epis == null) {

            backgroundJob(String.format(pattern, id, (s + 1), 1));
            if (epis != null) {

                DatabaseReference dbRef;

                if (s + 1 < 10) {
                    epCode += "S0" + (s + 1) + "E01";
                } else {
                    epCode += "S" + (s + 1) + "E01";
                }
                String iid = null;
                try {
                    iid = epis.getString("id");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                Log.d("EPISODE", "nasiel som epizodu na druhy pokus: " + epCode);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(epis.getString("air_date"));
                    if (date.getTime() < System.currentTimeMillis()) {
                        dbRef = FirebaseDatabase.getInstance().getReference("users/" + mail + "/series/" + id + "/" + "season_" + (s + 1));
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("1", iid);
                        dbRef.updateChildren(childUpdates);
                    } else {
                        return "You watched all aired episodes.";
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } else { // ak sa vobec nenasla epizoda
                Log.d("EPISODE", "Epizoda sa nenasla");
            }
        } else { // ak sa hned nasla epizoda

            if (s < 10) {
                epCode += "S0" + s;
            } else {
                epCode += "S" + s;
            }

            if (e + 1 < 10) {
                epCode += "E0" + (e + 1);
            } else {
                epCode += "E" + (e + 1);
            }
            String iid = null;
            try {
                iid = epis.getString("id");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(epis.getString("air_date"));
                if (date.getTime() < System.currentTimeMillis()) {
                    DatabaseReference dbRef;
                    dbRef = FirebaseDatabase.getInstance().getReference("users/" + mail + "/series/" + id + "/" + "season_" + s);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put((e + 1) + "", iid);
                    dbRef.updateChildren(childUpdates);
                } else {
                    return "You watched all aired episodes.";
                }
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


            Log.d("EPISODE", "nasiel som epizodu hned: " + epCode);
        }

        return null;
    }

    public void backgroundJob(String url) {
        String result;
        String inputLine;

        try {
            URL myUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        try {
            if (result != null) {
                epis = new JSONObject(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) Toast.makeText(MySeries.ctx, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
    }
}
