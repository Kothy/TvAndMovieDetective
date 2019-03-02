package com.example.klaud.tvandmoviedetective;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomOnItemSelectedListenerCities implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0){
            String url="https://tv-program.aktuality.sk/kino/"+parent.getItemAtPosition(position).toString();
            Log.d("URL",url);
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            Theatres.city=parent.getItemAtPosition(position).toString();
            GetHTMLTreeCity getHTML=new GetHTMLTreeCity();
            getHTML.execute(url);
            //Log.d("URL",url);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }


}
