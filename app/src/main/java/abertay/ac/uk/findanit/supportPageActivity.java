package abertay.ac.uk.findanit;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class supportPageActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String customerID = "";
    String customerphonenumber;
    boolean isLoggedout = false;
    private static final int NOTIFICATION_ID_TEXT = 1;
    public static final String CHANNEL_ID_IMPORTANT = "Job has been Canceled by customer";
    public static final String CHANNEL_ID_NORMAL = "New Job!";
    int PERMISSION_ID = 44;

    double latText, lonText;

    Marker jobMarker;

    NotificationManager notificationManager;
    Notification.Builder jobcanceled;
    Notification.Builder newjob;

    FusedLocationProviderClient mFusedLocationClient;

    private DatabaseReference assignedCustomerLocationRef;
    private ValueEventListener assignedCustomerLocationRefListener;

    private LinearLayout CustomerInfo;

    TextView custname, custphonenumber;
    Button logoutbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supportpage);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initNotificationChannels();

        jobcanceled = new Notification.Builder(getApplicationContext(), CHANNEL_ID_IMPORTANT)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Job Cancelled");


        newjob = new Notification.Builder(getApplicationContext(), CHANNEL_ID_NORMAL)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("New Job")
                .setAutoCancel(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        CustomerInfo = findViewById(R.id.customerInfo);
        custname = findViewById(R.id.customername);
        custphonenumber = findViewById(R.id.customerpnumber);

        logoutbtn = findViewById(R.id.logOutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggedout = true;
                disconnect();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(supportPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        getAssignedCustomer();


        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);


    }

    private void getAssignedCustomer() {

        String supportID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Support").child(supportID).child("customerJobID");
        //new job and job canceled listeners
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerID = dataSnapshot.getValue().toString();
                    getAssignedCustomerLocation();
                    notificationManager.notify(NOTIFICATION_ID_TEXT, newjob.build());
                    getAssignedCustomerInfo();
                } else {
                    customerID = "";

                    if (jobMarker != null) {
                        notificationManager.notify(NOTIFICATION_ID_TEXT, jobcanceled.build());
                        jobMarker.remove();
                        CustomerInfo.setVisibility(View.GONE);
                        custname.setText("");
                        custphonenumber.setText("");

                    }
                    if (assignedCustomerLocationRefListener != null) {


                        assignedCustomerLocationRef.removeEventListener(assignedCustomerLocationRefListener);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getAssignedCustomerInfo() {
        DatabaseReference customerInfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(customerID);
        customerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    CustomerInfo.setVisibility(View.VISIBLE);
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {

                        String name = map.get("name").toString();
                        name = name.replace("{", "");
                        String[] splitstr;
                        splitstr = name.split("=");

                        custname.setText(splitstr[0]);
                    }

                    if (map.get("pnumber") != null) {

                        String pnum = map.get("pnumber").toString();
                        pnum = pnum.replace("{", "");
                        String splitsrt[] = pnum.split("=");
                        customerphonenumber = splitsrt[0];
                        custphonenumber.setText(customerphonenumber);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getAssignedCustomerLocation() {

        assignedCustomerLocationRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(customerID).child("l");

        assignedCustomerLocationRefListener = assignedCustomerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !customerID.equals("")) {

                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0; //location latitude
                    double locationLon = 0; //location longitude
                    if (map.get(0) != null) {

                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null) {
                        locationLon = Double.parseDouble((map.get(1)).toString());

                    }
                    LatLng supportlng = new LatLng(locationLat, locationLon);

                    jobMarker = mMap.addMarker(new MarkerOptions().position(supportlng).title("Job Location"));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latText, lonText), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(supportlng)      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng jobmark = new LatLng(latText, lonText);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jobmark));
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng location = new LatLng(latText, lonText);
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latText, lonText), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latText, lonText))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("SupportLocation");
        GeoFire gfire = new GeoFire(myref);
        gfire.setLocation(userid, new GeoLocation(latText, lonText), new
                GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

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
                                    latText = location.getLatitude();
                                    lonText = location.getLongitude();
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
    private void requestNewLocationData() {

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
            onMapReady(mMap);

        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

    public void calluser(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + customerphonenumber));
        startActivity(intent);
    }

    public void cancelejob(View view){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Users").child("Support").child(userID).child("customerJobID");
        DatabaseReference customerref = FirebaseDatabase.getInstance().getReference("Requests").child(customerID);
        myref.removeValue();
        customerref.removeValue();
    }

    public void finishJob(View view){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Users").child("Support").child(userID).child("customerJobID");
        DatabaseReference customerref = FirebaseDatabase.getInstance().getReference("Requests").child(customerID);
        myref.removeValue();
        customerref.removeValue();
        jobcanceled.setContentTitle("Job finished");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
           // getAssignedCustomerLocation(); <-- this may get the marker back after calling user
        }

    }


    public void disconnect() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("SupportLocation").child(userID);
        myref.removeValue();

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (!isLoggedout) {
            disconnect();

        }

    }

    private void initNotificationChannels() {
        ArrayList<NotificationChannel> channelList = new ArrayList<>();
        channelList.add(new NotificationChannel(CHANNEL_ID_IMPORTANT, "IMPORTANT", NotificationManager.IMPORTANCE_HIGH));
        channelList.add(new NotificationChannel(CHANNEL_ID_NORMAL, "DEFAULT", NotificationManager.IMPORTANCE_DEFAULT));

        if (notificationManager != null)
            notificationManager.createNotificationChannels(channelList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}