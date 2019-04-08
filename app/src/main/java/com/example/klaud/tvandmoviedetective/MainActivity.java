package com.example.klaud.tvandmoviedetective;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DownloadManager downloadManager;
    ArrayList<Long> list = new ArrayList<>();
    ProgressDialog pd;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static ArrayList<String> movies=new ArrayList<>();
    public static ArrayList<String> series=new ArrayList<>();
    //public static ArrayList<String> persons=new ArrayList<>();
    String st[]={"movie_ids_","tv_series_ids_","person_ids_"};
    public static NavigationView navigationView;
    public static DrawerLayout drawer;
    public static TextView noInt;
    public static String mail=null;
    public static Context ctx;
    public static AppBarLayout appbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    SearchView searchView;
    static FragmentManager fragManager;
    ConstraintLayout mainLay;
    Boolean isEmptyFragVisible=false;

    @Override
    public void onResume() {
        super.onResume();
        Snackbar.make(mainLay, "onResume() main", Snackbar.LENGTH_LONG).show();
        if (isInternet()){
            if ((series.size() == 0 || movies.size() == 0)){
                //if (!pd.isShowing()) pd.show();
                checkIfExistFileAndUnpack();
                //if (pd.isShowing()) pd.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx=getApplicationContext();

        fragManager=getSupportFragmentManager();
        appbar = findViewById(R.id.barWithTabs);
        appbar.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        mainLay = findViewById(R.id.mainLay);

        Fragment face=new Facebook();
        pd=new ProgressDialog(this);

        pd.setTitle("Please wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        //pd.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large);
        pd.setMax(100);

        prefs = getSharedPreferences("INFO", MODE_PRIVATE);
        editor = getSharedPreferences("INFO", MODE_PRIVATE).edit();
        editor.putString("class","");
        editor.putString("search","");
        editor.apply();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.drawerEmailTextView);
        navUsername.setText("you are not logged in");

        noInt = findViewById(R.id.noInternet);
        noInt.setVisibility(View.INVISIBLE);
        isEmptyFragVisible = false;
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // toto budem este riesit
                int hasReadContactPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        checkIfExistFileAndUnpack();

        if (isInternet()) {
            noInt.setVisibility(View.INVISIBLE);
            isEmptyFragVisible = false;
            opennigFragment();
            if (Facebook.isLogged()) {
                if (MainActivity.prefs.getString("login","").equals("")) {
                    //Toast.makeText(ctx, "setujem z FACEBOOKU", Toast.LENGTH_SHORT).show();
                    Facebook.setMailToDrawer();
                }
                else {
                    //Toast.makeText(ctx, "setujem y preferencies", Toast.LENGTH_SHORT).show();
                    View headerView2 = MainActivity.navigationView.getHeaderView(0);
                    TextView navUsername2 = (TextView) headerView2.findViewById(R.id.drawerEmailTextView);
                    navUsername.setText(MainActivity.prefs.getString("login",""));
                }
            }
        }
        handleIntent(getIntent());

        new Thread(new Runnable() {
            public void run() {
                Boolean boo = false, foo = true;
                Looper.prepare();
                while (true){
                    if (isInternet() == false && boo == false){
                        //Toast.makeText(MainActivity.this, "toast", Toast.LENGTH_SHORT).show();
                        boo = true;
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new EmptyFragment());
                        ft.commit();
                        foo = false;
                        setTitle("No internet connection");
                        appbar.setVisibility(View.INVISIBLE);
                        Snackbar.make(mainLay, "No internet connection", Snackbar.LENGTH_LONG).show();
                    }
                    else if (isInternet()) boo = false;

                    if (MainActivity.mail == null) MainActivity.mail = MainActivity.prefs.getString("login","");
                    if (isInternet() && foo == false){
                        opennigFragment();
                        foo = true;

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }
    private void checkIfExistFileAndUnpack(){
        if (!fileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Detective/" + "movie_ids_" + getYesterdayDate() + ".json")
                && isInternet()){
            if (pd.isShowing() == false) pd.show();
            noInt.setVisibility(View.INVISIBLE);
            isEmptyFragVisible = false;
            download();
        } else if (isInternet()) {
            try {
                unregisterReceiver(onComplete);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e("Receiver Error", e.getMessage());
            }
            if (unpack.getStatus() != AsyncTask.Status.RUNNING && (series.size() == 0 || movies.size() == 0))
                unpack.execute(st[0] + getYesterdayDate(), st[1] + getYesterdayDate(), "load","load"); // iba nacitava reporty

            noInt.setVisibility(View.INVISIBLE);
            isEmptyFragVisible = false;
        } else {
            isEmptyFragVisible = true;
            noInt.setVisibility(View.VISIBLE);
        }

        //if (isInternet()) displaySelectedScreen(R.id.movie);

    }

    private void setupViewPager(ViewPager viewPager) {
        appbar.setVisibility(View.VISIBLE);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //viewPager.setSaveFromParentEnabled(false);
        adapter.addFragment(new MyMovies(),"Wish list");
        adapter.addFragment(new MyMoviesWatched(),"Watch list");

        viewPager.setAdapter(adapter);
    }

    public Boolean isInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
           return true;
        }
        else return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// vyber z troch bodiek
        int id = item.getItemId();

        if (id == R.id.action_settings && Facebook.isLogged()) {
            displaySelectedScreen(R.id.action_settings);
            return true;
        } else if(id == R.id.logOut){
            if (Facebook.isLogged()) {
                LoginManager.getInstance().logOut();
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.drawerEmailTextView);
                navUsername.setText("you are not logged in");
                opennigFragment();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Co sa stlaci v draweri - zobrazenie
        //Toast.makeText(ctx, ""+(item.getItemId()==R.id.nav_myMovies), Toast.LENGTH_SHORT).show();
        if (Facebook.isLogged()) displaySelectedScreen(item.getItemId());
        return true;
    }
    public  void opennigFragment(){
        Fragment fragment = null;
        if (Facebook.isLogged()){
            fragment = new MoviesResultSearch();
        } else {
            fragment = new Facebook();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    private void displaySelectedScreen(int itemId) {
        if (!isInternet()) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return;
        }
        Fragment fragment = null;
        appbar.setVisibility(View.INVISIBLE);
        viewPager.setAdapter(null);

        if (itemId==R.id.movie){
            fragment = new MoviesResultSearch();
        } else if (itemId == R.id.action_settings){
            fragment = new Settings();
        } else if (itemId == R.id.Tv_Series){// zobrazenie trending alebo vyhladavania
            fragment = new TvSeriesResultSearch();
        } else if (itemId==-9999){// detail filmu bude mat taketo idecko
            fragment = new MovieDetail();
        } else if (itemId ==- 8888){// facebook fragment bude mat taketo idecko
            fragment = new Facebook();
        } else if (itemId == R.id.nav_myMovies){
            setupViewPager(viewPager);
            fragment = new EmptyFragment();

        } else if (itemId == R.id.nav_myTv){
            fragment = new MySeries();
        } else if (itemId == R.id.nav_theatres){
            fragment =new Theatres();
        } else if (itemId == R.id.nav_friends){
            fragment= new Friends();
        } else if (itemId == -987654321){
            fragment= new MyMoviesWatched();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();

        Fragment fragment = null;
        //appbar.setVisibility(View.INVISIBLE);
        //viewPager.setAdapter(null);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if (prefs.getString("prev class","").contains("MyMovies")){
            setupViewPager(viewPager);
            displaySelectedScreen(R.id.nav_myMovies);

        } else if (prefs.getString("prev class","").equals("MovieDetail")){
            fragment = new MovieDetail();
            bundle.putString("id", MainActivity.prefs.getString("idBP",""));
            bundle.putString("title", MainActivity.prefs.getString("titleBP",""));

            fragment.setArguments(bundle);


        } else if (prefs.getString("prev class","").equals("SeriesDetail")){
            fragment = new SeriesDetails();
            bundle.putString("id", MainActivity.prefs.getString("idSeriesBP",""));
            //bundle.putString("title", MainActivity.prefs.getString("titleBP",""));

            fragment.setArguments(bundle);


        } else if (prefs.getString("prev class","").equals("Theatres")){
            displaySelectedScreen(R.id.nav_theatres);

        }   else if (prefs.getString("prev class","").equals("Friends")){
            displaySelectedScreen(R.id.nav_friends);

        } else if (prefs.getString("prev class","").equals("MovieResultSearch")){//
            displaySelectedScreen(R.id.movie);

        } else if (prefs.getString("prev class","").equals("SeriesResultSearch")){
            displaySelectedScreen(R.id.Tv_Series);

        } else if (prefs.getString("prev class","").equals("Settings")){
            displaySelectedScreen(R.id.action_settings);

        }  else if (prefs.getString("prev class","").equals("MySeries")){
            displaySelectedScreen(R.id.nav_myTv);

        } else if (prefs.getString("prev class","").equals("User profile")){
            fragment = new UserProfile();

            bundle.putString("nick", MainActivity.prefs.getString("nickBP",""));
            bundle.putString("email", MainActivity.prefs.getString("emailBP",""));

            fragment.setArguments(bundle);
        } else if (prefs.getString("prev class","").equals("Episodes")){
            fragment = new Episodes();

            bundle.putString("title", MainActivity.prefs.getString("titleEpBP",""));
            bundle.putString("id", MainActivity.prefs.getString("idEpBP",""));
            bundle.putInt("seasons", MainActivity.prefs.getInt("seasonsEpBP",-1));
            bundle.putString("poster_path", MainActivity.prefs.getString("poster_pathEpBP",""));
            bundle.putString("networks", MainActivity.prefs.getString("networksEpBP",""));

            fragment.setArguments(bundle);

        } else {
            moveTaskToBack(true);
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    void deleteAlllFilesInFolder(File dir) {
        if (dir.isDirectory()){
            for (File child : dir.listFiles()){
                child.delete();
            }
        }
    }

    public void download(){
        pd.show();
        deleteAlllFilesInFolder(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS)+"/Detective/"));
        for (int i = 0; i < 2; i++){
            Uri Download_Uri= Uri.parse("http://files.tmdb.org/p/exports/"+st[i]+getYesterdayDate()+".json.gz");
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle("Reports");
            request.setDescription("Downloading " + "reports for Detective");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS
                    + "/Detective"+"/", st[i]+getYesterdayDate()+".json.gz");
            list.add(downloadManager.enqueue(request));
        }
    }
    public String getYesterdayDate(){
        DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            list.remove(referenceId);
            if (list.isEmpty()) {
                unpack.execute(st[0]+getYesterdayDate(),st[1]+getYesterdayDate(),st[2]+getYesterdayDate());
                unregisterReceiver(onComplete);
            }
        }
    };

    AsyncTask<String, Integer, String> unpack = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            pd.show();
        }
        @Override
        protected String doInBackground(String... name) {
            if (Looper.myLooper() == null) Looper.prepare();
            if(name.length==4){
                loadSingle(name[0]);
                loadSingle(name[1]);
            } else {
                unpackSingle(name[0]);
                unpackSingle(name[1]);

                delete(name[0]);
                delete(name[1]);

                loadSingle(name[0]);
                loadSingle(name[1]);
            }
            return name.length+"";
        }
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            Log.d("Movies",movies.size()+" "+series.size());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.incrementProgressBy(values[0]);
        }
        protected void unpackSingle(String name){
            try {
                FileInputStream fis = new FileInputStream(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS)+"/Detective/"+name+".json.gz");
                GZIPInputStream gis = new GZIPInputStream(fis);
                FileOutputStream fos = new FileOutputStream(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS)+"/Detective/"+name+".json");
                byte[] buffer = new byte[1024];
                int len;
                while((len = gis.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }
                fos.close();
                gis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void delete(String name){
            String fileName =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Detective/"+name+".json.gz";
            File myFile = new File(fileName);
            if(myFile.exists())
                myFile.delete();
        }
        protected  void loadSingle(String name){
            if (name.contains("movie")) movies.clear();
            if (name.contains("series")) series.clear();
            try(BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Detective/"+name+".json"))) {
                String line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if (line!=null){
                        line=line.toLowerCase();
                        if (name.contains("movie")) movies.add(line);
                        if (name.contains("series")) series.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public Boolean fileExist(String fileName){
        File myFile = new File(fileName);
        return  myFile.exists();
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);

            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
    }

    private void showResults(String query) {
        editor.putString("search",query);
        //setTitle("Search results");
        if (!Facebook.isLogged()) return;
        if (prefs.getString("class","").contains("Movie")){
            displaySelectedScreen(R.id.movie);
        } else if (prefs.getString("class","").contains("Series")){
            displaySelectedScreen(R.id.Tv_Series);
        } else if (prefs.getString("class","").contains("Friends")){
            //Toast.makeText(ctx, "hladam v useroch: "+query, Toast.LENGTH_SHORT).show();
            Friends.searchResult(query);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
}
