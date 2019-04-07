package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Facebook extends Fragment {
    CallbackManager callbackManager;
    LoginButton loginButton;
    public static AccessToken accessToken;
    private static final String EMAIL = "email";
    public static String email="";
    static TextView drawerTV;
    public static FirebaseAuth mAuth;
    Context ctx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.facebook, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Log in");
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        loginButton.setFragment(this);
        drawerTV = view.findViewById(R.id.drawerEmailTextView);
        callbackManager = CallbackManager.Factory.create();
        ctx =view.getContext();
        mAuth = FirebaseAuth.getInstance();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken= AccessToken.getCurrentAccessToken();
                setMailToDrawer();

                /*ProgressDialog pd=new ProgressDialog(ctx);

                pd.setTitle("Logging in...");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCancelable(false);
                pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                pd.setMax(100);
                pd.show();*/

                //while (MainActivity.mail== null || MainActivity.mail.equals("")){}

                //pd.dismiss();
                openMovieFragment();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getContext(), "Prihlasenie sa zrusilo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                //Toast.makeText(getContext(), "Exception vyhodeny", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    public static AccessToken getAccessToken(){
        return AccessToken.getCurrentAccessToken();
    }

    public static Boolean isLogged(){
        AccessToken at= AccessToken.getCurrentAccessToken();
        return at != null && !at.isExpired();
    }

    private static void handleLogin(String mai) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mai, "000000")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(ctx, "prihlaseny uspesne", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            handleRegister(mai);
                            //Toast.makeText(ctx, "pouzivatel asi este neexistuje alebo chyba", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private static void handleRegister(String mai) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("users/"+MainActivity.mail.replace(".","_")+"/settings");
        handleLogin(mai);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("private", "false");
        childUpdates.put("nickname", "");
        childUpdates.put("friends", "");
        dbRef.updateChildren(childUpdates);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mai, "000000")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(ctx, "Uspesne zaregistrovany pouzivatel", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Toast.makeText(ctx, "Uzivatel asi uz existuje alebo nastala chyba", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void setMailToDrawer(){
        GraphRequest request = GraphRequest.newMeRequest(getAccessToken(), (JSONObject object, GraphResponse response) ->{
            try {
                MainActivity.mail=object.getString("email");
                email = object.getString("email");

                View headerView = MainActivity.navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.drawerEmailTextView);
                navUsername.setText(email);

                if (email!= null || !email.equals(null)) {
                    //Toast.makeText(MainActivity.ctx, "handlujem usera", Toast.LENGTH_SHORT).show();
                    handleLogin(email);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public  void openMovieFragment(){
        Fragment fragment = new MoviesResultSearch();
        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
