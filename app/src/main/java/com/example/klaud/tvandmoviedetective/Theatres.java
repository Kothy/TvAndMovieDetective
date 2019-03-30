package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class Theatres extends Fragment {

    public static Context ctx;
    static Spinner spinnerCities, spinnerTheatres;
    static ArrayList<String> theatres=new ArrayList<>();
    static ArrayList<String> urlForTheatres=new ArrayList<>();
    static ArrayAdapter<String> adapter2;
    static ArrayAdapter<String> adapter;
    static String city;
    public static RecyclerView recycler;
    public static TheatresAdapter rec_adapter;
    public static ArrayList<TheatresItem> items=new ArrayList<>();
    static TextView noProgram;

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

        theatres.clear();
        String[] cities={"Choose city", "Bratislava",
                "Košice", "Trnava", "Banská Bystrica", "Nitra",
                "Prešov", "Trenčín", "Žilina", "Bánovce nad Bebravou",
                "Banská Štiavnica", "Bardejovské Kúpele", "Brezno", "Bytča",
                "Častá", "Detva", "Dolný Kubín", "Dunajská Streda",
                "Galanta", "Handlová", "Hlohovec",
                "Hriňová", "Humenné", "Kežmarok", "Kremnica",
                "Krupina", "Kysucké Nové Mesto",
                "Levice", "Levoča", "Liptovský Mikuláš", "Lučenec",
                "Malacky", "Martin", "Michalovce", "Modra",
                "Myjava", "Námestovo", "Nová Dubnica", "Nováky",
                "Nové Mesto nad Váhom", "Nové Zámky",
                "Nový Smokovec", "Partizánske", "Pezinok", "Piešťany",
                "Poprad", "Prievidza", "Púchov", "Revúca",
                "Rimavská Sobota", "Ružomberok", "Sabinov", "Senec",
                "Senica", "Sereď", "Skalica", "Snina",
                "Sobrance", "Spišská N. Ves", "Stará Ľubovňa",
                "Stupava","Svidník","Šaľa",
                "Štúrovo", "Tatranská Lomnica", "Topoľčany", "Trebišov",
                "Trenčianske Teplice", "Turčianske Teplice",
                "Turzovka", "Tvrdošín", "Veľký Krtíš", "Vráble",
                "Vranov nad Topľou", "Zlaté Moravce", "Zvolen", "Žarnovica"};

        spinnerCities = view.findViewById(R.id.spinner);
        spinnerTheatres = view.findViewById(R.id.spinner2);

        theatres.add("Choose theatre");

        noProgram= view.findViewById(R.id.no_program);
        noProgram.setVisibility(View.INVISIBLE);

        adapter= new ArrayAdapter<>(ctx, R.layout.view_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapter);
        spinnerCities.setOnItemSelectedListener(new CustomOnItemSelectedListenerCities());

        adapter2= new ArrayAdapter<>(ctx, R.layout.view_spinner_item, theatres);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheatres.setAdapter(adapter2);
        spinnerTheatres.setOnItemSelectedListener(new CustomOnItemSelectedListenerTheatres());

        recycler = (RecyclerView) getView().findViewById(R.id.theatres_recycler);
        rec_adapter = new TheatresAdapter(getContext(), items);
        recycler.setAdapter(rec_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        spinnerTheatres.setVisibility(View.VISIBLE);
        spinnerCities.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
    }
}
