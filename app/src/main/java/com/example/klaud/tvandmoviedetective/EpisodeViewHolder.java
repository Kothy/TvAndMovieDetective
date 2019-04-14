package com.example.klaud.tvandmoviedetective;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class EpisodeViewHolder extends ChildViewHolder {
    Episode prod;
    private TextView mTextView, season, date;
    private ImageView iv;
    private View view;

    public EpisodeViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        mTextView = itemView.findViewById(R.id.season);
        iv = itemView.findViewById(R.id.itemImage2);
        season = itemView.findViewById(R.id.seaAndEpNum);
        date = itemView.findViewById(R.id.textView15);
    }

    public void bind(Episode episode) {

        mTextView.setText(episode.name);
        prod = episode;
        season.setText(prod.sea);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date.setText(sdf.format(episode.airDate));
        if (prod.checked) iv.setImageResource(R.drawable.checked);
        else iv.setImageResource(R.drawable.unchecked);
        view.setOnClickListener(click -> {
            Toast.makeText(MainActivity.ctx, "bude presmerovanie na popis epizody", Toast.LENGTH_SHORT).show();

        });
        iv.setOnClickListener(click -> {
            //Toast.makeText(MainActivity.ctx, episode.name+ " "+ episode.company, Toast.LENGTH_SHORT).show();
            if (prod.airDate.getTime() > System.currentTimeMillis()) {
                Toast.makeText(MainActivity.ctx, "Not aired episode", Toast.LENGTH_SHORT).show();
                return;
            }
            prod.reverseChecked();
            if (prod.checked) iv.setImageResource(R.drawable.checked);
            else iv.setImageResource(R.drawable.unchecked);

            if (episode.checked == true) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference("users/" +
                        MainActivity.mail.replace(".", "_")
                        + "/series/" + episode.series_id
                        + "/season_" + episode.season_id
                );
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(episode.ep_number + "", episode.episode_id);
                dbRef.updateChildren(childUpdates);

                dbRef = database.getReference("users/" +
                        MainActivity.mail.replace(".", "_")
                        + "/series/" + episode.series_id);
                childUpdates = new HashMap<>();
                childUpdates.put("name", episode.series_name);
                childUpdates.put("poster_path", episode.poster_path);
                childUpdates.put("networks", episode.network);
                childUpdates.put("rating", "");
                dbRef.updateChildren(childUpdates);

                String maiil = MainActivity.mail.replace(".", "_");
                dbRef = database.getReference("/users/" + maiil + "/recent/");
                childUpdates = new HashMap<>();
                childUpdates.put(System.currentTimeMillis() + "", " mark " + episode.series_name + " " + episode.toString() + " as watched");
                dbRef.updateChildren(childUpdates);
            } else {
                //Toast.makeText(MainActivity.ctx, "idem vymazat epiyodu s pozretych", Toast.LENGTH_SHORT).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference("users/" +
                        MainActivity.mail.replace(".", "_")
                        + "/series/" + episode.series_id
                        + "/season_" + episode.season_id + "/" + episode.ep_number
                );
                dbRef.removeValue();
                episode.checked = false;
                iv.setImageResource(R.drawable.unchecked);
            }
        });
    }

}
