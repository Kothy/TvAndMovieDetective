package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Friends extends Fragment {
    public static Context ctx;
    public static TextView tv;
    static DataSnapshot data=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("class","Friends");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.friends_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friends");
        ctx = getContext();
        tv = view.findViewById(R.id.friends_found);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail=MainActivity.mail.replace(".","_");
        DatabaseReference dbRef = database.getReference("users/");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data=dataSnapshot;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
    public static void searchResult(String query){
        tv.setText("");
        if (data!=null){
            for (DataSnapshot ds: data.getChildren()){
                if (ds.hasChild("settings/nickname") && !ds.child("settings/nickname").getValue().equals("")){
                    tv.append(ds.child("settings/nickname").getValue().toString()+System.lineSeparator());
                } else
                tv.append(ds.getKey().split("@")[0]+System.lineSeparator());
            }
        }
    }
}
