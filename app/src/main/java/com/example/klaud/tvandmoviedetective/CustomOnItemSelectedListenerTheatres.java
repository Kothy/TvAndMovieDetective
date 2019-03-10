package com.example.klaud.tvandmoviedetective;

import android.view.View;
import android.widget.AdapterView;

public class CustomOnItemSelectedListenerTheatres implements AdapterView.OnItemSelectedListener{
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position>0){
                //Toast.makeText(parent.getContext(), "OnItemSelectedListener: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                GetHTMLTreeProgram program=new GetHTMLTreeProgram();
                program.execute(Theatres.urlForTheatres.get(position));
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
