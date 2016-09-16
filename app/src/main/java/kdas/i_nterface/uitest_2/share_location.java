package kdas.i_nterface.uitest_2;

import android.Manifest;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class share_location extends AppCompatActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    boolean chk;


    FloatingActionButton fab1, fab2, fab_kill;
    FrameLayout frame1, frame2;

    String user_num, furl;
    boolean clicked = false;
    boolean started = false;
    boolean thread_kill = false;

    Thread run;

    Firebase user_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        getSupportActionBar().hide();

        Firebase.setAndroidContext(this);



        Log.d("##1","1");
        fab1 = (FloatingActionButton)findViewById(R.id.share_static);
        fab2 = (FloatingActionButton)findViewById(R.id.share_real);
        fab_kill = (FloatingActionButton)findViewById(R.id.kill);

        frame1 = (FrameLayout)findViewById(R.id.static_frame);
        frame2 = (FrameLayout)findViewById(R.id.real_time_frame);

        Log.d("##1","1");
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        user_num = pref.getString("Number", "");

        Log.d("##1","1");
        furl = "https://wifiap-1361.firebaseio.com/" + user_num + "/location";
        user_location = new Firebase(furl);

        Log.d("##1","1");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (chk = checkperm()){
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(4 * 1000); // 1 second, in milliseconds

            Log.d("##1","1");
        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            if (checkperm()){
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(4 * 1000); // 4 seconds, in milliseconds
            }
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked) {
                    frame1.setBackgroundColor(ContextCompat.getColor(frame1.getContext(), R.color.yellow));
                    clicked = true;
                }
                else {
                    frame1.setBackgroundColor(ContextCompat.getColor(frame1.getContext(), R.color.yellow));
                    frame2.setBackgroundColor(ContextCompat.getColor(frame2.getContext(), R.color.some_white));
                }

                if (!started) {


                    mGoogleApiClient.connect();
                    started = true;
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_kill.setVisibility(View.VISIBLE);
                if (!clicked) {
                    frame2.setBackgroundColor(ContextCompat.getColor(frame2.getContext(), R.color.yellow));
                    clicked = true;
                }
                else {
                    frame2.setBackgroundColor(ContextCompat.getColor(frame2.getContext(), R.color.yellow));
                    frame1.setBackgroundColor(ContextCompat.getColor(frame1.getContext(), R.color.some_white));
                }
                if (!started) {
                    run_thread();
                }

            }
        });

        fab_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread_kill = true;
                finish();
            }
        });




    }

    private void run_thread(){



        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (!thread_kill){
                    mGoogleApiClient.connect();
                    started = false;
                    Log.d("Keep", "running");
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void set(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public boolean checkperm(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (checkperm()){
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else {

            //denied, cant reach here if denied anyway, so hakuna-matata
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("test", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void handleLocation(final Location location)
    {
        Log.d("loc", location.toString());

        user_location.setValue(location);

    }

    @Override
    public void onLocationChanged(Location location)
    {
        handleLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
