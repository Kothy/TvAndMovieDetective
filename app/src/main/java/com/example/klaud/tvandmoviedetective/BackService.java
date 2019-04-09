package com.example.klaud.tvandmoviedetective;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackService extends Worker {

    public BackService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public  void sendNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.ctx, "9999")
                .setSmallIcon(R.drawable.ic_delete_black_24dp)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.ctx);
        notificationManager.notify(1, builder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
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
            if (MainActivity.dataSnap.hasChild("movies")){
                DataSnapshot movies = MainActivity.dataSnap.child("movies");
                Log.d("SKUSAM", movies.toString());

            }
            return Result.SUCCESS;
        } else {
            return Result.RETRY;
        }
    }
}

