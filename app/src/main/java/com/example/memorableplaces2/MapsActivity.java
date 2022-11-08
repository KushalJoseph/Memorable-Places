package com.example.memorableplaces2;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng toBeShownLatLng;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        int receivedId = getIntent().getIntExtra("id", -1);

        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng changedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(changedLocation));

                if(toBeShownLatLng != null)
                    mMap.addMarker(new MarkerOptions().position(toBeShownLatLng).title("Selected Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT < 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if(receivedId == -1) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
                } else {
                    String a = MainActivity.latLngStringList.get(receivedId);
                    //Of the form: "lat/lng:(a,b)"
                    String[] b = a.split("\\(");
                    //To string manipulate a bracket "(", put it after "//" as shown.
                    String[] c = b[1].split("\\)");
                    String[] d = c[0].split(",");
                    double selectedLatitude = Double.parseDouble(d[0]);
                    double selectedLongitude = Double.parseDouble(d[1]);
                    toBeShownLatLng = new LatLng(selectedLatitude, selectedLongitude);

                    mMap.addMarker(new MarkerOptions().position(toBeShownLatLng)
                            .title(MainActivity.placesList.get(receivedId))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(toBeShownLatLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toBeShownLatLng,14));

                    Location locationA = new Location("user current");
                    locationA.setLongitude(userLocation.longitude);
                    locationA.setLatitude(userLocation.latitude);

                    Location locationB = new Location("selected");
                    locationB.setLatitude(selectedLatitude);
                    locationB.setLongitude(selectedLongitude);

                    float distance = locationA.distanceTo(locationB);
                    double roundOff = (double) Math.round(distance * 100.0) / 100.0;

                    String printvar = "";
                    if (roundOff < 1000) {
                        printvar = roundOff + " meters";
                    } else {
                        printvar= roundOff / 1000 + " km";
                    }
                    Toast.makeText(this, "Approximate distance from current location:\n" + printvar,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList.size()>0 && addressList.get(0)!=null) {
                address = addressList.get(0).getAddressLine(0);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        String finalAddress = address;
        new Handler().postDelayed(() -> {
            Intent addPlaceActivity = new Intent(getApplicationContext(), AddPlace.class);
            addPlaceActivity.putExtra("address", finalAddress);
            addPlaceActivity.putExtra("latLng", latLng.toString());
            startActivity(addPlaceActivity);
        }, 2000);
    }
}
