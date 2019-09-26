package com.example.mymeetings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class AttendanceActivity extends AppCompatActivity implements OnMapReadyCallback {


    final static int REQUEST_LOCATION = 199;
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;

    public List<Address> address = new ArrayList<>();
    LocationCallback locationCallback;
    String addressStr;
    Button markAttendancebtn,meetingAdminbtn;
    TextView txt_location;
    TextView emp_name_toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myref;
    ProgressBar progressBar;
    Button admin_login_button;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //Turn on Gps
    private GoogleApiClient googleApiClient;
    Double lat = 0.0;
    Double longi = 0.0;

    LatLng latlaong;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //  Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    getDeviceLocation();
                    return true;
                }
            });
        }

    }
    //Oncreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_maps);
        // Session class instance
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        txt_location = (TextView) findViewById(R.id.txt_location);
        getLocationPermission();

        //Turn on Gps
        final LocationManager manager = (LocationManager) AttendanceActivity.this.getSystemService(Context.LOCATION_SERVICE);
        //Turn on Gps
        if (!hasGPSDevice(AttendanceActivity.this)) {
            Toast.makeText(AttendanceActivity.this, "GPS not supported.", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(AttendanceActivity.this)) {
            Log.e("Suvrat", "Gps already enabled");
            Toast.makeText(AttendanceActivity.this, "GPS not enabled.", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            Log.e("Suvrat", "Gps already enabled");
            //    Toast.makeText(AttendanceActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }

    }//end of on Create

    //Turn on Gps
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onStart() {
        getDeviceLocation();

        super.onStart();

    }

    //Turn on Gps
    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(AttendanceActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(3 * 1000);
            locationRequest.setFastestInterval(3 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(AttendanceActivity.this, REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                    }
                }
            });
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Log.d("Latitude",""+currentLocation.getLatitude());
                            Log.d("Longitude",""+currentLocation.getLongitude());
                           // Toast.makeText(AttendanceActivity.this, ""+currentLocation.getLatitude()+" Long"+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                           // Snackbar.make(AttendanceActivity.this,""+currentLocation.getSpeed(),Snackbar.LENGTH_SHORT).show();
                            try {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);

                            } catch (Exception e) {
                                Toast.makeText(AttendanceActivity.this, "Turn on Permission", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(AttendanceActivity.this, "Your location is unavailable.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


        lat = latLng.latitude;
        longi = latLng.longitude;
        buildLocationCallback(lat, longi);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(AttendanceActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }


    private void buildLocationCallback(final Double lat, final Double lon) {

        if (lat > 0 && lon > 0) {

            Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            try {
                address = geoCoder.getFromLocation(lat, lon, 1);
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i = 0; i <= maxLines; i++) {
                    addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append("");
                }

                txt_location.setText(addressStr);
                Button button = findViewById(R.id.btn_markAttendance);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AttendanceActivity.this,Attendance_home.class);
                        intent.putExtra("fetchedLocation",addressStr);
                        startActivity(intent);
                    }
                });
                meetingAdminbtn = findViewById(R.id.btn_meetingAdmin);
                meetingAdminbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            progressBar = (ProgressBar) findViewById(R.id.progressBar2);
                        //the aftermath of the admin button
                        progressBar.setVisibility(View.VISIBLE);


                        firebaseDatabase = FirebaseDatabase.getInstance();
                        myref=firebaseDatabase.getReference();
                        myref.child("admin").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String db_id = (String) dataSnapshot.child("id").getValue();
                                final String db_password = (String) dataSnapshot.child("password").getValue();
                                //Custom admin login dialog-------
                                AlertDialog.Builder mbuilder = new AlertDialog.Builder(AttendanceActivity.this);
                                final View mView = getLayoutInflater().inflate(R.layout.dialog_admin_login,null);

                                admin_login_button = (Button) mView.findViewById(R.id.admin_login_button);
                                admin_login_button.setOnClickListener(new View.OnClickListener() {


                                    @Override
                                    public void onClick(View v) {

                                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                                Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(admin_login_button.getWindowToken(), 0);

                                        EditText id = (EditText) mView.findViewById(R.id.admin_id);
                                        EditText password = (EditText)mView.findViewById(R.id.admin_password);
                                        if (id.getText().toString().equals("")&& password.toString().equals("")){
                                            Toast.makeText(AttendanceActivity.this, "Fill all the entries", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(id.getText().toString().equals(db_id)&& password.getText().toString().equals(db_password)){
                                            Toast.makeText(AttendanceActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AttendanceActivity.this,Admin_home.class);
                                            i.putExtra("loc123",addressStr);
                                            startActivity(i);
                                        }
                                        else
                                            Toast.makeText(AttendanceActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();




                                    }
                                });
                                mbuilder.setView(mView);
                                AlertDialog dialog = mbuilder.create();
                                progressBar.setVisibility(View.INVISIBLE);
                                dialog.show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });




            } catch (IOException e) {
            } catch (NullPointerException e) {
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        getDeviceLocation();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }


}
