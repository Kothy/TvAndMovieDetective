package com.example.klaud.tvandmoviedetective;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackService extends Worker {

    public BackService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public void sendNotification(String text, String title) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.ctx, "9999")
                .setSmallIcon(R.drawable.ic_search_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Random rnd = new Random();
        Integer randomId = rnd.nextInt((10000000 - 1) + 1) + 1;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.ctx);
        notificationManager.notify(randomId, builder.build());



    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("9999", "NotifChannel", importance);
            channel.setDescription("Blah Blah Notification");
            NotificationManager notificationManager = MainActivity.notificationManager;
            notificationManager.createNotificationChannel(channel);
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        if (MainActivity.dataSnap != null) {
            if (MainActivity.dataSnap.hasChild("movies")) {
                if (MainActivity.dataSnap.hasChild("movies")) {
                    DataSnapshot movies = MainActivity.dataSnap.child("movies");
                    for (DataSnapshot movie : movies.getChildren()) {
                        if (movie.hasChild("status") && movie.child("status").getValue().toString().equals("want")) {
                            if (movie.hasChild("release_date")) {
                                Long movieMillisec = Long.decode(movie.child("release_date").getValue().toString());

                                //sendNotification("SENT FROM BACKSERVICE", movie.child("title").getValue().toString());

                                if (movieMillisec > System.currentTimeMillis()) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                                    Date currDate = new Date(System.currentTimeMillis());
                                    Date movieDate = new Date(movieMillisec);

                                    Calendar c2 = Calendar.getInstance();
                                    c2.setTime(currDate);
                                    c2.add(Calendar.DATE, +2);
                                    currDate = c2.getTime();

                                    if (sdf.format(movieDate).equals(sdf.format(currDate))) {

                                        sendNotification("In theatres in 2 days. SENT FROM BACKSERVICE", movie.child("title").getValue().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Log.d("POKUS", "Worker's job is done.");
            return Result.SUCCESS;
        } else {
            Log.d("POKUS", "Worker's job is done.");
            return Result.RETRY;
        }
    }

    public static class sendNotif extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            //httppost.setHeader("Authorization", "Bearer "+accessToken);



            return null;
        }
        private void readJSONObjectFromUrlPOST(String text, String title) throws IOException, JSONException {


            MainActivity.getFirebaseRegistationId();

            // 1. URL
            URL url = new URL("https://android.googleapis.com/gcm/send");
            // 2. Open connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 3. Specify POST method
            urlConnection.setRequestMethod("POST");
            // 4. Set the headers
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "key=AIzaSyA6lAr4jnly1EtHpNfB1i_8Z12EcUvAudg");
            urlConnection.setDoOutput(true);
            // 5. Add JSON data into POST request body
            JSONObject obj = null;
            String json= "{\"data\": {" +
                    "\"title\": \"" + title + "\"," +
                    "\"text\": \""+ text + "\"" +
                    "}," +
                    "\"to\" : \"" + MainActivity.prefs.getString("FToken", "") + "\"" +
                    "}";
            try {
                obj = new JSONObject(json);
                //Log.d("VZORJSONA", obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 6. Get connection output stream
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(obj.toString());
            out.close();
            // 6. Get the response
            /*int responseCode = urlConnection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();*/
            //Log.d("GCM getResponseCode:", new Integer(responseCode).toString());

        }
    }
}

