package com.example.klaud.tvandmoviedetective;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomOnItemSelectedListenerCities implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0){
            String url="https://tv-program.aktuality.sk/kino/";

            Theatres.city=parent.getItemAtPosition(position).toString();
            GetHTMLTreeCity getHTML=new GetHTMLTreeCity();
            getHTML.execute(url+edit(Theatres.city));
            //Theatres.items.clear();
            Theatres.rec_adapter.notifyDataSetChanged();
            Theatres.recycler.invalidate();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public String edit(String city) {
        String result = "";
        city=city.toLowerCase();
        for (int i = 0; i < city.length(); i++) {
            if (city.charAt(i) == 'š') result += "s";
            else if (city.charAt(i) == ' ') result += "-";
            else if (city.charAt(i) == 'á') result += "a";
            else if (city.charAt(i) == 'č') result += "c";
            else if (city.charAt(i) == 'í') result += "i";
            else if (city.charAt(i) == 'ž') result += "z";
            else if (city.charAt(i) == 'š') result += "s";
            else if (city.charAt(i) == 'ú') result += "u";
            else if (city.charAt(i) == 'ý') result += "y";
            else if (city.charAt(i) == 'ň') result += "n";
            else if (city.charAt(i) == 'é') result += "e";
            else if (city.charAt(i) == 'ď') result += "d";
            else if (city.charAt(i) == 'ť') result += "t";
            else if (city.charAt(i) == 'ľ') result += "l";
            else if (city.charAt(i) == '.') result += "";
            else result+=city.charAt(i);
        }
        return result.toLowerCase();
    }
}
