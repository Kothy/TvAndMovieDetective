package com.example.klaud.tvandmoviedetective;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("job")
    @Expose
    public String job;

    @SerializedName("name")
    @Expose
    public String name;

}
