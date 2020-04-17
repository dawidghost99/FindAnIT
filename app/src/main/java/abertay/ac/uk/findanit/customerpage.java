package abertay.ac.uk.findanit;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class customerpage extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String address;

    Button requestBtn, logoutbtn;




    double latText,lonText;

    private LatLng jobLocation;

    private Boolean requestBool = false;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerpage);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        requestBtn = findViewById(R.id.calljobbtn);
        logoutbtn = findViewById(R.id.logoutbtn);



        getLastLocation();


        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(requestBool){
                    requestBool = false;
                    gQuery.removeAllListeners();

                    if(supportFoundID != null){
                        DatabaseReference SupportRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Support").child(supportFoundID).child("customerJobID");
                        SupportRef.removeValue();
                        supportFoundID = null;


                    }
                    supportFound = false;
                    radius = 1;

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference requestref = FirebaseDatabase.getInstance().getReference().child("Requests").child(userID);
                    requestref.removeValue();



                    if(jobLocation != null){
                        mMap.clear();

                    }

                    requestBtn.setText("Request I.T. Specialist");



                }else{
                    requestBool = true;
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
                    GeoFire gfire = new GeoFire(ref);
                    gfire.setLocation(userID, new GeoLocation(latText, lonText),new
                            GeoFire.CompletionListener(){
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    jobLocation = new LatLng(latText,lonText);
                                    mMap.addMarker(new MarkerOptions().position(jobLocation).title("Your location"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(jobLocation));

                                }
                            });

                    requestBtn.setText("Looking for  I.T. ... ");
                    //requestBtn.setEnabled(false);
                    getClosestSupport();

                }


            }
        });





    }

    private int radius = 1;
    private boolean supportFound = false ;
    private String supportFoundID;
    GeoQuery gQuery;

    private void getClosestSupport(){
        DatabaseReference supportLocation = FirebaseDatabase.getInstance().getReference("SupportLocation");
        GeoFire gfire = new GeoFire(supportLocation);


        gQuery = gfire.queryAtLocation(new GeoLocation(latText,lonText),radius);
        gQuery.removeAllListeners();


        gQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!supportFound && requestBool){

                    supportFound = true;
                    supportFoundID = key;

                    DatabaseReference SupportRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Support").child(supportFoundID);
                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerJobID", customerID);
                    SupportRef.updateChildren(map);
                    requestBtn.setText("I.T. found - Click here to cancel your I.T. service");

                    //getSupportLocation();


                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!supportFound) {
                    //radius upto 20 KM
                    radius++;
                    getClosestSupport();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    /*

    private Marker supportMarker;
    private DatabaseReference supportref;
    private ValueEventListener support;


    private void getSupportLocation(){


        supportref = FirebaseDatabase.getInstance().getReference().child("SupportLocation").child(supportFoundID).child("l");
        supportref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLon = 0;
                    requestBtn.setText("Support found");
                    if(map.get(0) != null){

                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if(map.get(1) != null){
                        locationLon = Double.parseDouble((map.get(1)).toString());

                    }

                    LatLng supportlng = new LatLng(locationLat,locationLon);

                    if(supportMarker != null){
                        supportMarker.remove();

                    }

                    Location location1 =  new Location("");
                    location1.setLatitude(jobLocation.latitude);
                    location1.setLongitude(jobLocation.longitude);


                    Location location2 =  new Location("");
                    supportMarker = mMap.addMarker(new MarkerOptions().position(supportlng).title("Your I.T. Support Location"));
                    location2.setLatitude(jobLocation.latitude);
                    location2.setLongitude(jobLocation.longitude);


                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng location = new LatLng(latText, lonText);
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latText, lonText),13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latText, lonText))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }



    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latText= location.getLatitude() ;
                                    lonText=location.getLongitude();
                                    mMap.clear();

                                    onMapReady(mMap);

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }







    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latText = mLastLocation.getLatitude();
            lonText = mLastLocation.getLongitude();
            mMap.clear();
            onMapReady(mMap);

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }



    @Override
    protected void onStop(){
        super.onStop();

        //disconnect



    }


    public void logout(View V){

         FirebaseAuth.getInstance().signOut();
         Intent intent = new Intent(customerpage.this, MainActivity.class);
         startActivity(intent);
         finish();


    }





}
