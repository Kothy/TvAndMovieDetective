package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Episodes extends Fragment {
    public Context ctx;
    Integer Id=-1;
    Integer numOfseasons=-1;
    String showtitle;
    public static ArrayList<MovieItem> items= new ArrayList<>();
    public static RecyclerView recyclerView;
    public static EpisodeAdapter adapter;
    public String poster_path, networks;
    ArrayList<Season> companies = new ArrayList<>();
    DataSnapshot dSnapshot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class",MainActivity.prefs.getString("class",""));
        MainActivity.editor.putString("class","Episodes");
        MainActivity.editor.apply();
        MainActivity.appbar.setVisibility(View.VISIBLE);
        MainActivity.viewPager.setVisibility(View.VISIBLE);
        MainActivity.tabLayout.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.episodes_lay, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Episodes");
        recyclerView = view.findViewById(R.id.recyclerviewEp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ctx=getContext();

        Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        MainActivity.appbar.setVisibility(View.INVISIBLE);

    }
    public Boolean isEpisodeChecked(Integer seriesId, String season, Integer epNum){
        if (dSnapshot.hasChild(""+seriesId)){
            if (dSnapshot.child(""+seriesId).hasChild(season)){
                if (dSnapshot.child(""+seriesId).child(season).hasChild(""+epNum)){
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail=MainActivity.mail.replace(".","_");
        DatabaseReference dbRef = database.getReference("users/"+mail+"/series");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dSnapshot=dataSnapshot;
                if (bundle != null ) {
                    showtitle=bundle.getString("title", "");
                    getActivity().setTitle(showtitle+"'s episodes");
                    String movieID = bundle.getString("id", "");
                    numOfseasons=bundle.getInt("seasons", -1);
                    Id=Integer.valueOf(movieID);
                    poster_path=bundle.getString("poster_path");
                    networks=bundle.getString("networks");

                    MainActivity.editor.putString("titleEpBP",showtitle);
                    MainActivity.editor.putString("idEpBP",movieID);
                    MainActivity.editor.putInt("seasonsEpBP",numOfseasons);
                    MainActivity.editor.putString("poster_pathEpBP",poster_path);
                    MainActivity.editor.putString("networksEpBP",networks);
                    MainActivity.editor.apply();

                    String pattern="https://api.themoviedb.org/3/tv/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&append_to_response=";
                    for (int i=1;i<=numOfseasons;i++){
                        pattern+="season/"+i+",";
                    }
                    getJsonString.execute(String.format(pattern, Id));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    AsyncTask<String, Integer, String> getJsonString = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
        }
        @Override
        protected String doInBackground(String... params){
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            try {
                JSONObject jsonSeries =new JSONObject(result);
                String seasonName=jsonSeries.getString("name");
                String poster_path=jsonSeries.getString("poster_path");
                Integer id=jsonSeries.getInt("id");
                Season compa=new Season("Seasons",new ArrayList<>());

                companies.add(compa);
                for(int j=0;j<=numOfseasons;j++){

                    if (jsonSeries.has("season/"+j)){
                        ArrayList<Episode> episodes=new ArrayList<>();
                        JSONObject sea=jsonSeries.getJSONObject("season/"+j);
                        JSONArray seaEps=sea.getJSONArray("episodes");
                        String seaName=sea.getString("name");
                        Log.d("seasonsAndEps",seaName);
                        for(int i=0;i<seaEps.length();i++){
                            JSONObject episod=seaEps.getJSONObject(i);
                            String epName=episod.getString("name");
                            Log.d("seasonsAndEps","      "+epName);
                            String epNum=String.valueOf(episod.getInt("episode_number"));

                            if (epNum.length()<2) epNum="0"+epNum;
                            String seaNu=String.valueOf(j);
                            Integer seaNumber=j;
                            if (seaNu.length()<2) seaNu="0"+seaNu;
                            Episode prod=new Episode(epName,seasonName,"S"+seaNu+"E"+epNum,
                                    id,sea.getInt("season_number"),
                                    episod.getInt("id"),
                                    episod.getInt("episode_number"),
                                    seasonName, poster_path, networks
                            );
                            //Log.d("EPISODES","Series id: "+prod.series_id+" series num: "+" ep number: "+prod.ep_number);

                            prod.checked=isEpisodeChecked(prod.series_id,"season_"+seaNumber,prod.ep_number);
                            episodes.add(prod);
                        }
                        Season comp=new Season(seaName,episodes);
                        companies.add(comp);
                        Log.d("seasonsAndEps",episodes.size()+"");
                        Log.d("seasonsAndEps","------------------------------------------------------------");
                    }

                }
                adapter = new EpisodeAdapter(companies);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
