package com.example.klaud.tvandmoviedetective;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cast {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("character")
    @Expose
    public String character;
}
