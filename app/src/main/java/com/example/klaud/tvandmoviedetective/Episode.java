package com.example.klaud.tvandmoviedetective;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Episode implements Parcelable {
    public final String name;
    public String company;
    public Boolean checked=false;
    public String sea;
    public Integer season_id;
    public Integer episode_id;
    public Integer series_id;
    public Integer ep_number;
    public String series_name;
    public String poster_path;
    public String network;
    public Date airDate;

    public Episode(String name, String company, String seaAndEpNum, Integer series_id, Integer sea_id, Integer ep_id, Integer ep_num, String series_name, String poster, String network, Date air) {
        this.sea = seaAndEpNum;
        this.name = name;
        this.company = company;
        this.season_id = sea_id;
        this.episode_id = ep_id;
        this.series_id = series_id;
        this.ep_number = ep_num;
        this.series_name = series_name;
        this.poster_path = poster;
        this.network = network;
        this.airDate = air;
    }

    public void reverseChecked() { this.checked = !this.checked; }

    protected Episode(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };
    public String toString(){
        return sea;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
