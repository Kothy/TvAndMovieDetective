package com.example.klaud.tvandmoviedetective;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.HashMap;
import java.util.Map;

public class EpisodeViewHolder extends ChildViewHolder {
    private TextView mTextView, season;
    private ImageView iv;
    private View view;
    Episode prod;
    public EpisodeViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        mTextView = itemView.findViewById(R.id.season);
        iv = itemView.findViewById(R.id.itemImage);
        season=itemView.findViewById(R.id.seaAndEpNum);
    }

    public void bind(Episode episode) {

        mTextView.setText(episode.name);
        prod= episode;
        season.setText(prod.sea);
        if (prod.checked) iv.setImageResource(R.drawable.checked);
        else iv.setImageResource(R.drawable.unchecked);
        view.setOnClickListener(click -> {
            Toast.makeText(MainActivity.ctx, "bude presmerovanie na popis epizody", Toast.LENGTH_SHORT).show();

        });
        iv.setOnClickListener(click ->{
            //Toast.makeText(MainActivity.ctx, episode.name+ " "+ episode.company, Toast.LENGTH_SHORT).show();
            prod.reverseChecked();
            if (prod.checked) iv.setImageResource(R.drawable.checked);
            else iv.setImageResource(R.drawable.unchecked);

            if (episode.checked==true){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference("users/"+
                        MainActivity.mail.replace(".","_")
                        +"/series/"+episode.series_id
                        +"/season_"+episode.season_id+"/"+episode.ep_number
                );
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("id",episode.episode_id);
                dbRef.updateChildren(childUpdates);
            }
            else {
                Toast.makeText(MainActivity.ctx, "idem vymazat epiyodu s pozretych", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
