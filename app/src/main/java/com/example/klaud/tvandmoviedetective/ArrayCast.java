package com.example.klaud.tvandmoviedetective;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArrayCast {
    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("cast")
    @Expose
    public List<Cast> cast = new ArrayList<Cast>();

    @SerializedName("crew")
    @Expose
    public List<Crew> crew = new ArrayList<Crew>();
}
