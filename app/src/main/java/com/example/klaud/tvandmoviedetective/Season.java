package com.example.klaud.tvandmoviedetective;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Season extends ExpandableGroup<Episode> {
    public Season(String title, List<Episode> items) {
        super(title, items);
    }
}
