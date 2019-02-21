package com.example.klaud.tvandmoviedetective;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class EpisodeAdapter extends ExpandableRecyclerViewAdapter<SeriesViewHolder, EpisodeViewHolder> {
    public EpisodeAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public SeriesViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_recyclerview_company, parent, false);
        return new SeriesViewHolder(v);
    }

    @Override
    public EpisodeViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout2, parent, false);
        return new EpisodeViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(EpisodeViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Episode episode = (Episode) group.getItems().get(childIndex);
        holder.bind(episode);

    }

    @Override
    public void onBindGroupViewHolder(SeriesViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Season season = (Season) group;
        holder.bind(season);
    }

}
