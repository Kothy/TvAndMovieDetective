package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SaveToFirebase extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(strings[0]);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("status", strings[1]);
        childUpdates.put("title", strings[2]);
        dbRef.updateChildren(childUpdates);


        //dbRef =database.getReference("users/kada11@azet_sk/movies/297802");
        //dbRef.toString();

        return null;
    }
    @Override
    protected void onPostExecute(String result){

    }

    @Override
    protected void onPreExecute() {
    }
}
