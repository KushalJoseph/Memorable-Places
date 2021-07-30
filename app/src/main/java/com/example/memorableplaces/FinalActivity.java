package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FinalActivity extends AppCompatActivity {
    EditText editText;
    String address;
    String latLngString;

    public void setDefaultAddress(View view)
    {
        if(address!=null)
        editText.setText(address);
        else
            Toast.makeText(this, "This place doesn't have an address. Please add your own name.", Toast.LENGTH_SHORT).show();
    }

    public void clickOk(View view)
    {
        if(editText.getText().length()==0)
        {
            Toast.makeText(this, "Please enter a name for your place", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String placeName=editText.getText().toString();
            MainActivity.placesList.add(placeName);
            Toast.makeText(this, "Location added!", Toast.LENGTH_SHORT).show();
            MainActivity.latLngStringList.add(latLngString);
            MainActivity.arrayAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences=this.getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
            try {
                sharedPreferences.edit().putString("places",ObjectSerializer.serialize( MainActivity.placesList)).apply();
                sharedPreferences.edit().putString("latLngs",ObjectSerializer.serialize(MainActivity.latLngStringList)).apply();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            //Converts placesList and latLngStringList to a STRING to save it.
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }


    }
    public void clickCancel(View view)
    {
        Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
        intent.putExtra("id",-1);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        editText=(EditText)findViewById(R.id.editText);

        Intent intent=getIntent();
        address=intent.getStringExtra("address");
        latLngString=intent.getStringExtra("latLng");


    }
}
