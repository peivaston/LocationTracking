package com.org.locationtracking.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.R;
import com.org.locationtracking.databinding.ActivityMapBinding;
import com.org.locationtracking.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import lombok.val;

//******************************************************
public class MapActivity
        extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener
//******************************************************
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityMapBinding mBinding;

    //create google map
    private GoogleMap mGoogleMap;


    private static final String MAP_VIEW_BUNDLE_KEY = "MAP_VIEW_BUNDLE_KEY";

    //use to show boundry on google map
    private LatLngBounds.Builder mBuilder;

    //contains child id want to track
    private String mUserId;

    //used to get parcelable string from previous screen
    public static final String USER_ID = "USER_ID";

    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************
    {
        super.onCreate(savedInstanceState);

        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        //init google map
        initGoogleMap(savedInstanceState);

        //call function that contains main logic
        initControls();
    }

    //******************************************************
    private void initControls()
    //******************************************************
    {
        //get parcelable data send from previous screen
        getParcelable();

        //get child location
        getChildLocation();

        //emergency call button click listener
        mBinding.emergencyCall.setOnClickListener(view -> showEmergencyDialog());
    }

    private void showEmergencyDialog()
    {
        //show emrgenecy calling dialof
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.emergency_call_dialog);
        Button fireDepartment = dialog.findViewById(R.id.fire_deparment);
        Button police = dialog.findViewById(R.id.police_department);
        Button embulance = dialog.findViewById(R.id.ambulance);

        //fire department click listener
        fireDepartment.setOnClickListener(view -> {
            if (checkCallPermission())
            {
                callPhone("110");
            }
        });

        //police department click listener
        police.setOnClickListener(view -> {

            if (checkCallPermission())
            {
                callPhone("112");
            }

        });

        //embulance click listener
        embulance.setOnClickListener(view -> {
            if (checkCallPermission())
            {
                callPhone("113");
            }

        });


        //show dialog
        dialog.show();

    }

    //call  to given number
    private void callPhone(String number)
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    //check call permission
    private boolean checkCallPermission()
    {


        if (ContextCompat.checkSelfPermission(this,
                                              Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {

            //if call permission is not granted .than get run time permission
            ActivityCompat.requestPermissions(this,
                                              new String[] { Manifest.permission.CALL_PHONE },
                                              1);

            //return from function
            return false;
        }
        return true;
    }
    //******************************************************
    private void getParcelable()
    //******************************************************
    {
        //if data exist
        if (getIntent().getExtras()
                       .containsKey(USER_ID))
        {
            //assign child id to mUserId
            mUserId = getIntent().getStringExtra(USER_ID);
        }
    }

    //******************************************************
    private void getChildLocation()
    //******************************************************
    {


        //create firestore instance add snapshot this will autmatically trigger when child location changed
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("id", mUserId)
                   .addSnapshotListener(
                           new EventListener<QuerySnapshot>()
                           {
                               @Override
                               public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                               {
                                   if (queryDocumentSnapshots.isEmpty())
                                       return;


                                   //get child list
                                   List<User> users = new ArrayList<>();
                                   for (val child : queryDocumentSnapshots.getDocuments())
                                   {
                                       users.add(child.toObject(User.class));
                                   }

                                   //show poly line on map
                                   showPolyLineOnMap(users);
                               }
                           });
    }

    //******************************************************************
    private void showPolyLineOnMap(List<User> locationList)
    //******************************************************************
    {
        //if google map is empty return from function
        if (mGoogleMap == null) return;

        //clear google map .of stations are already exist clear all stations
        mGoogleMap.clear();

        // initialize google map boundry
        mBuilder = new LatLngBounds.Builder();

        //lat/long list contains stations latitude and longitude
        List<LatLng> latLngList = new ArrayList<>();

        //get list of station from location class which is application class
        for (val station : LocationApp.instance()
                                         .getStationList())
        {
            //get station latitude and parse string to double
            val lat = Double.parseDouble(station.getLatitude());

            //get station latitude and parse string to double
            val lng = Double.parseDouble(station.getLongitude());

            //create latlng variable
            LatLng latLng = new LatLng(lat, lng);

            //add marker on stattion with station name
            addMarkerToMap(station.getStationName(),
                           "",
                           lat,
                           lng,
                           0);

            //add lat/long to list
            latLngList.add(latLng);

            //include station coordinates in map boundry
            mBuilder.include(latLng);

        }

        // draw poly line between station
        mGoogleMap.addPolyline(new PolylineOptions().addAll(latLngList));

        //add user image on google map
        mGoogleMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(locationList.get(0)
                                                                      .getLatitude(),
                                                          locationList.get(0)
                                                                      .getLongitude()))
                                     .title(locationList.get(0)
                                                        .getUserName())
                                     .snippet(locationList.get(0)
                                                          .getUserName())
                                     .icon(bitmapDescriptorFromVector(getApplicationContext(),
                                                                      R.drawable.kidmarker))
                                     .zIndex(5));

        LatLngBounds bounds = mBuilder.build();
        int padding = 100;
        //adjust camera according to stations
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        //animate camera
        mGoogleMap.animateCamera(cu);

    }

    //convert drawable to bitmap for adding custom marker
    //**********************************************************************************
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    //**********************************************************************************
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                                 vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                                            vectorDrawable.getIntrinsicHeight(),
                                            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //add marker to map
    //******************************************************************
    private void addMarkerToMap(String title, String snippet, double lat, double lng, int index)
    //******************************************************************
    {
        //add marker to map on given  coordinates
        LatLng sportsLocation = new LatLng(lat, lng);
        mGoogleMap.addMarker(new MarkerOptions()
                                     .position(sportsLocation)
                                     .title(title)
                                     .snippet(snippet)
                                     .icon(getMarkerIcon("#465AAE"))
                                     .draggable(false)
                                     .visible(true))
                  .setTag(index);
    }

    //set marker color
    public BitmapDescriptor getMarkerIcon(String color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    //resume map when screen resume
    //******************************************************************
    @Override
    public void onResume()
    //******************************************************************
    {
        super.onResume();
        mBinding.mapview.onResume();

    }

    //stop map when screen destroy
    //******************************************************************
    @Override
    public void onStop()
    //******************************************************************
    {
        super.onStop();
        mBinding.mapview.onStop();
    }

    //set map setting
    //******************************************************************
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map)
    //******************************************************************
    {
        mGoogleMap = map;
        //enable gesture on map
        mGoogleMap.getUiSettings()
                  .setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings()
                  .setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings()
                  .setAllGesturesEnabled(true);
    }

    //******************************************************
    private void initGoogleMap(Bundle savedInstanceState)
    //******************************************************************
    {
        //init google map bundle
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
        {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mBinding.mapview.onCreate(mapViewBundle);
        mBinding.mapview.getMapAsync(this);
    }

    //******************************************************
    @Override
    public void onInfoWindowClick(Marker marker)
    //******************************************************
    {

    }
}
