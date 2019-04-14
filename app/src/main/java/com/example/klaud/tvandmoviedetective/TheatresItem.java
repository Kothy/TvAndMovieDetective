package com.example.klaud.tvandmoviedetective;

public class TheatresItem {
    public String title, pg, length, times, date;

    TheatresItem(String tit, String pg, String len, String tim, String date) {
        this.date = date;
        this.pg = pg;
        this.title = tit;
        this.length = len;
        this.times = tim;
    }
}
