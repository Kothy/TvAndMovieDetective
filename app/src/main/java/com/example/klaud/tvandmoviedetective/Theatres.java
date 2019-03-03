package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class Theatres extends Fragment {

    public static Context ctx;
    static Spinner spinnerCities, spinnerTheatres;
    static TextView tv;
    static ArrayList<String> theatres=new ArrayList<>();
    static ArrayList<String> urlForTheatres=new ArrayList<>();
    static ArrayAdapter<String> adapter2;
    static ArrayAdapter<CharSequence> adapter;
    static String city,theatre;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","Theatres");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.theatres_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Theatres");
        ctx=getContext();
        spinnerCities = view.findViewById(R.id.spinner);
        spinnerTheatres = view.findViewById(R.id.spinner2);
        adapter = ArrayAdapter.createFromResource(ctx, R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapter);
        spinnerCities.setOnItemSelectedListener(new CustomOnItemSelectedListenerCities());

        theatres.add("Choose theatre");
        adapter2= new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, theatres); //
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheatres.setAdapter(adapter2);
        spinnerTheatres.setSelection(0);
        spinnerTheatres.setOnItemSelectedListener(new CustomOnItemSelectedListenerTheatres());

    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
    }
}
