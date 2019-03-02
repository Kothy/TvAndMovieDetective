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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DownloadManager downloadManager;
    ArrayList<Long> list = new ArrayList<>();
    ProgressDialog pd;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static ArrayList<String> movies=new ArrayList<>();
    public static ArrayList<String> series=new ArrayList<>();
    public static ArrayList<String> persons=new ArrayList<>();
    String st[]={"movie_ids_","tv_series_ids_","person_ids_"};
    public static NavigationView navigationView;
    public static DrawerLayout drawer;
    public static TextView noInt;
    public static String mail=null;
    private static FirebaseAuth mAuth;
    public static Context ctx;
    public static AppBarLayout appbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appbar =findViewById(R.id.barWithTabs);
        appbar.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuth = FirebaseAuth.getInstance();
        Fragment face=new Facebook();
        pd=new ProgressDialog(this);
        ctx=getApplicationContext();
        pd.setTitle("Please wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
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
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // toto budem este riesit
                int hasReadContactPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (!fileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Detective/" + "movie_ids_" + getYesterdayDate() + ".json")
                && isInternet()){
            //Toast.makeText(this, "subor neexistuje - stahujem a rozbalujem a nacitavam", Toast.LENGTH_LONG).show();
            noInt.setVisibility(View.INVISIBLE);
            download();
        } else if (isInternet()) {
            unregisterReceiver(onComplete);
            if (unpack.getStatus()!=AsyncTask.Status.RUNNING) unpack.execute(st[0]+getYesterdayDate(),st[1]+getYesterdayDate(),"load","load"); // iba nacitava reporty
            //Toast.makeText(this, "subor existuje--*---*---*--- iba nacitavam", Toast.LENGTH_LONG).show();
            noInt.setVisibility(View.INVISIBLE);
        } else {
            noInt.setVisibility(View.VISIBLE);
        }

        //if (isInternet()) displaySelectedScreen(R.id.movie);
        if (isInternet()) {
            noInt.setVisibility(View.INVISIBLE);
            opennigFragment();
            if (Facebook.isLogged()) {
                Facebook.setMailToDrawer();
            }
        }
        handleIntent(getIntent());
    }

    private void setupViewPager(ViewPager viewPager) {
        appbar.setVisibility(View.VISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyMovies(), "Want to watch");
        adapter.addFragment(new MyMoviesWatched(), "Watched");
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(prefs.getString("class","").contains("Movie")){
                displaySelectedScreen(R.id.movie);
            }
            else if (prefs.getString("class","").contains("Series")){
                displaySelectedScreen(R.id.Tv_Series);
            }
        }
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

        if (id == R.id.action_settings) {
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

        Fragment fragment = null;
        appbar.setVisibility(View.INVISIBLE);
        viewPager.setAdapter(null);

        if (itemId==R.id.movie){
            fragment = new MoviesResultSearch();
        }
        else if (itemId==R.id.Tv_Series){// zobrazenie trending alebo vyhladavania
            fragment = new TvSeriesResultSearch();
        }
        else if (itemId==-9999){// detail filmu bude mat taketo idecko
            fragment = new MovieDetail();
        }
        else if (itemId==-8888){// facebook fragment bude mat taketo idecko
            fragment = new Facebook();
        } else if (itemId == R.id.nav_myMovies){
            setupViewPager(viewPager);
            fragment = new EmptyFragment();

        } else if (itemId == R.id.nav_myTv){
            fragment = new MySeries();
        } else if(itemId== R.id.nav_theatres){
            fragment =new Theatres();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
            Log.d("Movies",movies.size()+" "+persons.size()+" "+series.size());
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
            if (name.contains("tv")) series.clear();
            if (name.contains("person")) persons.clear();
            try(BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Detective/"+name+".json"))) {
                String line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if (line!=null){
                        line=line.toLowerCase();
                        if (name.contains("movie")) movies.add(line);
                        if (name.contains("tv")) series.add(line);
                        if (name.contains("person")) persons.add(line);
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
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
