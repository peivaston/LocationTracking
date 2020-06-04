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
import android.view.View;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.R;
import com.org.locationtracking.databinding.ActivityChildBinding;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.LocationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import lombok.val;

public class ChildActivity
        extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener

{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityChildBinding mBinding;

    //this is utility class for accessing current child location
    private LocationUtils mLocationUtils;

    //create google map
    private GoogleMap mGoogleMap;

    private static final String MAP_VIEW_BUNDLE_KEY = "MAP_VIEW_BUNDLE_KEY";

    //use to show boundry on google map
    private LatLngBounds.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_child);
        mBinding.textView4.setVisibility(View.GONE);

        //init google map
        initGoogleMap(savedInstanceState);

        //call function that contains main logic
        initControls();
    }


    private void initControls()

    {
        //init location utils
        mLocationUtils = LocationUtils.getInstance(ChildActivity.this);

        //set childname to childname textview
        mBinding.childName.setText(LocationApp.instance()
                                              .getUser()
                                              .getUserName());

        //stop journey click listener
        mBinding.stopJourney.setOnClickListener(view -> {


            //hide mapview
            mBinding.mapview.setVisibility(View.GONE);

            //hide stopjourney button
            mBinding.stopJourney.setVisibility(View.GONE);

            //hide startjourney button
            mBinding.startJourney.setVisibility(View.GONE);

            //unchecked location enable button
            mBinding.locationEnable.setChecked(false);

            //show location enable button
            mBinding.locationEnable.setVisibility(View.VISIBLE);

            //stop the location change listener
            mLocationUtils.stopListener();

            mBinding.imageView6.setVisibility(View.VISIBLE);

            mBinding.textView4.setVisibility(View.GONE);

        });

        //start journey click listener
        mBinding.startJourney.setOnClickListener(view -> {

            //show map view
            mBinding.mapview.setVisibility(View.VISIBLE);

            //show stop journey button
            mBinding.stopJourney.setVisibility(View.VISIBLE);

            mBinding.imageView6.setVisibility(View.GONE);


            //hide start journey button
            mBinding.startJourney.setVisibility(View.GONE);

            mBinding.textView4.setVisibility(View.GONE);

            //start location update listener
            attachLocationUpdateListener();
        });

        //location enable click listener
        mBinding.locationEnable.setOnClickListener(view -> {

            //check if location switch is checked
            if (mBinding.locationEnable.isChecked())
            {
                //check location permission is granted or not
                if (ContextCompat.checkSelfPermission(this,
                                                      Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {

                    //if location permission is not granted .than get run time permission
                    ActivityCompat.requestPermissions(this,
                                                      new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                                      1);
                    //uncheck the location enable switch
                    mBinding.locationEnable.setChecked(false);

                    //return from function
                    return;
                }

                //location permission is already granted

                //hide location enable button
                mBinding.locationEnable.setVisibility(View.GONE);

                //show start journey button
                mBinding.startJourney.setVisibility(View.VISIBLE);

                mBinding.textView4.setVisibility(View.VISIBLE);
            }
        });

        //logout click listener
        mBinding.logout.setOnClickListener(view -> logoutUser());

        //emergency call button click listener
        mBinding.emergencyCall.setOnClickListener(view -> showEmergencyDialog());
    }

    private void showEmergencyDialog()
    {
        //show emergency calling dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.emergency_call_dialog);
        Button fireDepartment = dialog.findViewById(R.id.fire_deparment);
        Button police = dialog.findViewById(R.id.police_department);
        Button ambulance = dialog.findViewById(R.id.ambulance);

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

        //ambulance click listener
        ambulance.setOnClickListener(view -> {
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

    //logout user function
    private void logoutUser()
    {
        //goto child screen to login screen
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);

        //destroy the child screen
        finish();
    }

    //******************************************************
    private void attachLocationUpdateListener()
    //******************************************************
    {
        //attach location update listener
        mLocationUtils.getCurrentLocation(true,//true if you want continues location update
                                          5000,//minimum time to check location update( 5 sec)
                                          1,// minimum distance is 1 meter
                                          new LocationUtils.CurrentLocationListener()
                                          {
                                              //this function is for getting last known location
                                              @Override
                                              public void onLastKnownLocation(double latitude, double longitude)
                                              {
                                              }

                                              //call back function for location update
                                              @Override
                                              public void onLocationUpdate(double latitude, double longitude)
                                              {
                                                  //get user detail from application class Location using instance methis
                                                  val user = LocationApp.instance()
                                                                        .getUser();
                                                  //save user latitude
                                                  user.setLatitude(latitude);

                                                  //save user longitude
                                                  user.setLongitude(longitude);

                                                  //get station list form application class
                                                  val stationList = LocationApp.instance()
                                                                               .getStationList();

                                                  //get last station detail from station list
                                                  val lastStattion = stationList.get(
                                                          stationList.size() - 1);


                                                  //calcualte distance bbetween current location and last stattion location
                                                  val distance = LocationUtils.distance(latitude,
                                                                                        longitude,
                                                                                        Double.parseDouble(
                                                                                                lastStattion.getLatitude()),
                                                                                        Double.parseDouble(
                                                                                                lastStattion.getLongitude()));

                                                  // if distance is less tha 20km
                                                  if (distance < 20)
                                                  {
                                                      //send notification to parent and familt member
                                                      sendNotification(user.getParentId());
                                                  }

                                                  //save user detail with current location in database
                                                  LocationApp.instance()
                                                             .getMFireStore()
                                                             .collection("Parent")
                                                             .document(user.getId())
                                                             .set(user);


                                                  //create user list
                                                  List<User> u = new ArrayList<>();
                                                  u.add(user);

                                                  //show poly line between stations
                                                  showPolyLineOnMap(u);
                                              }

                                              @Override
                                              public void onFailed()
                                              {

                                              }
                                          });
    }


    //send notification to parent and member
    private void sendNotification(String parentId)
    {
        //parent and member fcm ids through which they receive notification
        List<String> idsLIst = new ArrayList<>();


        //create firestore instance with collection parent and get parent detail
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .document(parentId)
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                   {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot)
                       {

                           //after getting parent detail
                           idsLIst.add(documentSnapshot.toObject(User.class)
                                                       .getFcmToken());

                           //create firestore instance get list of  member that contains parent id
                           //this members list are allowed to  track child location
                           LocationApp.instance()
                                      .getMFireStore()
                                      .collection("Parent")
                                      .whereArrayContains("sharedBy", parentId)
                                      .whereEqualTo("child", false)
                                      .get()
                                      .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                                      {
                                          @Override
                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                          {
                                              //get members list
                                              for (val child : queryDocumentSnapshots.getDocuments())
                                              {

                                                  //add fcm token of member to list
                                                  idsLIst.add(child.toObject(User.class)
                                                                   .getFcmToken());
                                              }

                                              HashMap<String, Object> data = new HashMap<>();

                                              //add child detail to hashmap
                                              data.put("child", LocationApp.instance()
                                                                           .getUser());
                                              //add fcm list to hash mao
                                              data.put("list", idsLIst);

                                              //add user detail and fcm list on notification table
                                              LocationApp.instance()
                                                         .getMFireStore()
                                                         .collection("Notification")
                                                         .document()
                                                         .set(data)
                                                         .addOnSuccessListener(
                                                                 aVoid -> {
                                                                 });

                                          }
                                      });

                       }
                   });

    }


    //show polyline on map with station list
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
    //******************************************************
    public BitmapDescriptor getMarkerIcon(String color)
    //******************************************************
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
    //*******************************************************
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
