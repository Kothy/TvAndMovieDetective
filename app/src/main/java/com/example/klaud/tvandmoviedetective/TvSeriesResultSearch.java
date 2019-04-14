package com.example.klaud.tvandmoviedetective;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TvSeriesResultSearch extends Fragment {
    public static ResultSearchAdapterSeries searchAdapter;
    public static ArrayList<SeriesItem> searchedItems = new ArrayList<>();
    static List<HashMap<String, String>> aList = Collections.synchronizedList(new ArrayList());
    static ArrayList<JSONObject> tvTrend = new ArrayList<>();
    static ArrayList<JSONObject> tvAir = new ArrayList<>();
    static ArrayList<SeriesItem> itemsTrending = new ArrayList<>();
    static ArrayList<SeriesItem> itemsAiring = new ArrayList<>();
    static SimpleAdapter simpleAdapter;
    static Context ctx;
    static RecyclerView recycler, recycler2, searchRecycler;
    static SeriesAdapter adapter, adapter2;
    static Activity activity;
    static FragmentManager fm;
    static ArrayList<DetailsForSearch> searchPool = new ArrayList<>();
    //static ListView lv;
    ProgressDialog pd;
    TextView trendingTitle, airingTitle;
    AsyncTask<String, Integer, String> getJsonTrending = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            String inputLine;
            try {
                URL myUrl = new URL("https://api.themoviedb.org/3/trending/tv/day?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            } catch (IOException e) {
                //Toast.makeText(ctx, "nastala nejaka chyba", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject js = new JSONObject(result);
                JSONArray arr = js.getJSONArray("results");
                for (int i = 0; i < arr.length(); i++) {

                    tvTrend.add(arr.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            Collections.sort(tvTrend, compareJSONObject());
            displayList();
        }
    };
    AsyncTask<String, Integer, String> getJsonAiring = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            String inputLine;
            try {
                URL myUrl = new URL("https://api.themoviedb.org/3/tv/airing_today?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&page=1");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            } catch (IOException e) {
                //Toast.makeText(ctx, "nieco je zle", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject js = new JSONObject(result);
                JSONArray arr = js.getJSONArray("results");
                for (int i = 0; i < arr.length(); i++) {

                    tvAir.add(arr.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();

            Collections.sort(tvAir, compareJSONObject());

            displayList2();
        }
    };

    public static void doMySearch(String query) throws JSONException {
        if (MainActivity.series.size() == 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(System.currentTimeMillis()));
            cal.add(Calendar.DATE, -1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy");

            String dateString = simpleDateFormat.format(cal.getTime());

            FillInfoForSearchSeries fillSeries = new FillInfoForSearchSeries();
            fillSeries.execute(query, dateString);
            return;
        }

        ArrayList<JSONObject> found = new ArrayList<>();
        query = query.toLowerCase();

        for (int i = 0; i < MainActivity.series.size(); i++) {
            String text = MainActivity.series.get(i);
            if (text != null && text.contains(query)) {
                JSONObject js = new JSONObject(text);
                Log.d("Hladanie", text + "");
                found.add(js);

            }
        }
        Collections.sort(found, compareJSONObject());
        aList.clear();
        searchedItems.clear();
        for (JSONObject js : found) {
            Log.d("Hladanie", js.toString());
            SeriesItem si = new SeriesItem(js.getString("original_name"),
                    R.drawable.nopicture, js.getInt("id"));
            si.setPoster_path("null");
            searchedItems.add(si);
            String patt = "https://api.themoviedb.org/3/tv/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            int pos = searchedItems.size() - 1;
            if (pos == -1) pos = 0;
            DetailsForSearch ds = new DetailsForSearch();
            searchPool.add(ds);
            ds.execute(String.format(patt, js.getInt("id")), pos + "", "series");
        }
        searchRecycler.invalidate();
        searchAdapter.notifyDataSetChanged();
    }

    public static Comparator<JSONObject> compareJSONObject() {
        Comparator comp = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    Double d1 = o1.getDouble("popularity");
                    Double d2 = o2.getDouble("popularity");
                    return d2.compareTo(d1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        return comp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class", MainActivity.prefs.getString("class", ""));
        MainActivity.editor.putString("class", "SeriesResultSearch");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.movies_search_result_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.prefs.getString("search", "").equals("")) {
            getActivity().setTitle("Tv Series");
        } else getActivity().setTitle("");

        ctx = getContext();
        activity = getActivity();
        fm = getFragmentManager();

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        trendingTitle = view.findViewById(R.id.textView3);
        airingTitle = view.findViewById(R.id.textView);
        airingTitle.setText("Airing today");
        trendingTitle.setText("Trending now");

        recycler = (RecyclerView) getView().findViewById(R.id.recycler);
        adapter = new SeriesAdapter(getContext(), itemsTrending, getFragmentManager(), getActivity());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler2 = (RecyclerView) getView().findViewById(R.id.recycler2);
        adapter2 = new SeriesAdapter(getContext(), itemsAiring, getFragmentManager(), getActivity());
        recycler2.setAdapter(adapter2);
        recycler2.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        searchRecycler = (RecyclerView) getView().findViewById(R.id.searchRecycler);
        searchAdapter = new ResultSearchAdapterSeries(getContext(), searchedItems, getFragmentManager(), getActivity());
        searchRecycler.setAdapter(searchAdapter);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        pd = new ProgressDialog(getView().getContext());
        pd.setTitle("Loading Data");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.setMax(100);

        String[] from = {"listview_image", "listview_title", "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
        //lv = (ListView) getView().findViewById(R.id.list);

        simpleAdapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_activity, from, to);
        //lv.setAdapter(simpleAdapter);
        if (!MainActivity.prefs.getString("search", "").equals("")) {

            searchRecycler.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.INVISIBLE);
            recycler2.setVisibility(View.INVISIBLE);
            airingTitle.setVisibility(View.INVISIBLE);
            trendingTitle.setVisibility(View.INVISIBLE);
            try {
                doMySearch(MainActivity.prefs.getString("search", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainActivity.editor.putString("search", "");
            MainActivity.editor.apply();
        } else { // ak sa zobrazuje iba trending

            searchRecycler.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.VISIBLE);
            recycler2.setVisibility(View.VISIBLE);
            airingTitle.setVisibility(View.VISIBLE);
            trendingTitle.setVisibility(View.VISIBLE);


            if (tvTrend.size() > 0) displayList();
            else getJsonTrending.execute();

            if (tvAir.size() > 0) displayList2();
            else getJsonAiring.execute();
        }
        /*lv.setOnItemClickListener((AdapterView<?> adapt, View viev, int pos, long arg3) -> {
            Fragment fragment = null;
            fragment = new SeriesDetails();
            Bundle bundle = new Bundle();
            bundle.putString("id", aList.get(pos).get("id"));
            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });*/

    }

    public void displayList() {
        itemsTrending.clear();
        try {
            for (JSONObject json : tvTrend) {
                SeriesItem si = new SeriesItem(json.getString("name"), R.drawable.nopicture, json.getInt("id"));// zmenene z "original_name"
                si.setPoster_path(json.getString("poster_path"));
                itemsTrending.add(si);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        recycler.invalidate();
    }

    public void displayList2() {
        itemsAiring.clear();
        try {
            for (JSONObject json : tvAir) {
                SeriesItem si = new SeriesItem(json.getString("name"), R.drawable.nopicture, json.getInt("id"));
                si.setPoster_path(json.getString("poster_path"));
                itemsAiring.add(si);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter2.notifyDataSetChanged();
        recycler2.invalidate();
    }
}
