package com.ingenious.Map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationTracker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // private MapController mapController;
    double lat,lng;
   static int NHT;


    //private Button btn1,btn2,btn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nht;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if ((!gpsStatus) && (!networkStatus)) {
            //---display the "Location services" settings page---
            Toast.makeText(this, "Enable Location Services and then relaunch Location Tracker", Toast.LENGTH_LONG).show();
            Intent in = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(in);
            finish();
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);


        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              return;
        }
        Location l = locationManager.getLastKnownLocation(provider);

        updateWithNewLocation(l);
        locationManager.requestLocationUpdates(provider, 60000, 0,locationListener);
    }

    public void onClick(View v)
    {
        if(v.getId()==R.id.b1)
            this.NHT=0;
        else if(v.getId()==R.id.b2)
            this.NHT=1;
        else if(v.getId()==R.id.b3)
            this.NHT=2;
        Toast.makeText(getBaseContext(),"Rotate Your Device to Refresh Google Maps",Toast.LENGTH_SHORT).show();
    }


    private void updateWithNewLocation(Location location) {
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.myLocationText);
        String latLongString = "No location found.";
        String addressString = "No address found.\n\nRotate your device to refresh.";
        double lat,lng;
        if (location != null) {

            lat = location.getLatitude();
            lng = location.getLongitude();
            latLongString = "Latitude		: " + lat + "\nLongitude	: " + lng;
            this.lng=lng;
            this.lat=lat;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (IOException e) {}
        }

        if((latLongString == "No location found."))
        {
            Toast.makeText(this, "Enable GPS Service",Toast.LENGTH_SHORT).show();

        }
        if(addressString == "No address found.\n\nRotate your device to refresh.")
        {
            Toast.makeText(this, "Enable Internet Access",Toast.LENGTH_SHORT).show();
        }
        myLocationText.setText(latLongString+"\n\n"+addressString);
    }


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       final float zoom=15.0f;
      GoogleMapOptions GMOptions=new GoogleMapOptions();
        int MapCode=this.NHT;
        switch(MapCode)
        {
            case 0: mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        LatLng pos = new LatLng(this.lat, this.lng);
        mMap.getMaxZoomLevel();
        mMap.setBuildingsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(pos).title("I am here."));
      mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }
}
