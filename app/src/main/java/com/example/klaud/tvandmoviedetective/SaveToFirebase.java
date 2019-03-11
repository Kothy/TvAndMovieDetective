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
        childUpdates.put("poster_path", strings[3]);
        if (strings.length>4){
            childUpdates.put("rating", strings[4]);
        }

        dbRef.updateChildren(childUpdates);
        return "done";
    }
    @Override
    protected void onPostExecute(String result){

    }

    @Override
    protected void onPreExecute() {
    }
}
