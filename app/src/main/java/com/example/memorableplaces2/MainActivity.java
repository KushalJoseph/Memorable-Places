package com.example.memorableplaces2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ListView listView;
    static ArrayList<String> placesList;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> latLngStringList;

    public void goToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("id", -1);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        placesList = new ArrayList<>();
        latLngStringList = new ArrayList<>();
        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
            ArrayList<String> placesSet = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            ArrayList<String> latLngSet = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latLngs", ObjectSerializer.serialize(new ArrayList<String>())));
            if(placesSet.size() > 0 && latLngSet.size() > 0) {
                placesList = new ArrayList<>(placesSet);
                latLngStringList = new ArrayList<>(latLngSet);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<> (this, android.R.layout.simple_list_item_activated_1, placesList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
            intent1.putExtra("id", position);
            startActivity(intent1);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.alert_dark_frame)
                    .setTitle("Delete place?")
                    .setMessage("Are you sure you want to delete this place?")
                    .setNegativeButton("NO",null)
                    .setPositiveButton("YES", (dialog, which) -> {
                        placesList.remove(position);
                        latLngStringList.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
                        try {
                            sharedPreferences.edit().putString("places", ObjectSerializer.serialize( MainActivity.placesList)).apply();
                            sharedPreferences.edit().putString("latLngs", ObjectSerializer.serialize(MainActivity.latLngStringList)).apply();
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .show();
            return true;
        });
    }
}