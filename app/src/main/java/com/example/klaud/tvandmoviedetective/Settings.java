package com.example.klaud.tvandmoviedetective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings extends Fragment {
    public static Context ctx;
    public EditText et;
    Button button, clearRecent;
    Switch swit;
    DataSnapshot data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class",MainActivity.prefs.getString("class",""));
        MainActivity.editor.putString("class","Settings");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Settings");
        ctx = getContext();
        et = view.findViewById(R.id.editText);
        button = view.findViewById(R.id.button9);
        swit = view.findViewById(R.id.switch1);
        clearRecent = view.findViewById(R.id.clear_recent_button);

        if (MainActivity.mail.equals("kada11@azet.sk")){
            clearRecent.setVisibility(View.VISIBLE);
        }
        else clearRecent.setVisibility(View.INVISIBLE);

        clearRecent.setOnClickListener( click ->{
            if (data != null){
                for (DataSnapshot ds: data.getChildren()){
                    if (ds.hasChild("recent")){

                        ArrayList<DataSnapshot> children = new ArrayList<>();
                        for (DataSnapshot child: ds.child("recent").getChildren()){
                            children.add(child);
                        }
                        //Toast.makeText(ctx, "pocet deti: "+children.size(), Toast.LENGTH_SHORT).show();
                        while(children.size() > 5){
                            children.get(0).getRef().removeValue();
                            children.remove(0);
                        }
                    }
                }
            }

        });

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        button.setOnClickListener(click ->{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String mail=MainActivity.mail.replace(".","_");
            DatabaseReference dbRef = database.getReference("users/"+mail+"/settings");
            //Toast.makeText(ctx, et.getText()+" "+swit.isChecked(), Toast.LENGTH_SHORT).show();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("nickname", et.getText().toString());
            childUpdates.put("private",swit.isChecked()+"");
            dbRef.updateChildren(childUpdates);
        });
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) { }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail=MainActivity.mail.replace(".","_");
        DatabaseReference dbRef = database.getReference("users/"+mail+"/settings");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("nickname") && !dataSnapshot.child("nickname").getValue().toString().equals("")){
                    et.setText(dataSnapshot.child("nickname").getValue().toString());
                }
                if (dataSnapshot.hasChild("private")){
                    if (dataSnapshot.child("private").getValue().equals("false")) {
                        swit.setChecked(false);
                    }
                    else {
                        swit.setChecked(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        dbRef = database.getReference("users/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data=dataSnapshot;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
