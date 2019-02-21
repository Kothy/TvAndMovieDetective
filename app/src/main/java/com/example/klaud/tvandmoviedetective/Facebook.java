package com.example.klaud.tvandmoviedetective;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Facebook extends Fragment {
    CallbackManager callbackManager;
    LoginButton loginButton;
    public static AccessToken accessToken;
    private static final String EMAIL = "email";
    public static String email="";
    static TextView tv;
    static TextView drawerTV;
    static View mainView;
    Button btn;
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
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setFragment(this);
        tv=view.findViewById(R.id.textView2);
        drawerTV = view.findViewById(R.id.drawerEmailTextView);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getContext(), "Prihlasenie sa podarilo", Toast.LENGTH_SHORT).show();
                accessToken= AccessToken.getCurrentAccessToken();
                setMailToDrawer();
                openMovieFragment();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Prihlasenie sa zrusilo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "Exception vyhodeny", Toast.LENGTH_SHORT).show();
            }
        });
        btn=view.findViewById(R.id.button2);
        btn.setOnClickListener(click -> {
            Toast.makeText(getContext(), ""+isLogged(), Toast.LENGTH_SHORT).show();

        });
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static AccessToken getAccessToken(){
        return AccessToken.getCurrentAccessToken();
    }
    public static Boolean isLogged(){
        AccessToken at= AccessToken.getCurrentAccessToken();
        return at != null && !at.isExpired();
    }
    public static void setMailToDrawer(){
        GraphRequest request = GraphRequest.newMeRequest(getAccessToken(), (JSONObject object, GraphResponse response) ->{
            try {
                email = object.getString("email");
                View headerView = MainActivity.navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.drawerEmailTextView);
                navUsername.setText(email);
                MainActivity.mail=email;

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
