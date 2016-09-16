package kdas.i_nterface.uitest_2;

import android.Manifest;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Marker marker, pinger_marker;
    Toast testtoast;

    private String dirresponse, query_url = "http://maps.googleapis.com/maps/api/directions/json?origin=26.1861458,91.7535008&destination=26.1834051,91.78202229999999";
    String poly_S;
    double distance;
    private java.util.List<LatLng> dpoly = new ArrayList<>();

    String test;
    String user_number, pinger_num, pinger_url ;

    Location current, pinger_location;

    boolean chk, once = false;
    boolean met = false;

    Firebase user, flocation, pinger, notif, notif_pinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Firebase.setAndroidContext(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            pinger_num = extras.getString("pinger_num");
            pinger_url = "https://wifiap-1361.firebaseio.com/" + pinger_num;
        }


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
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            if (checkperm()){
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(4 * 1000); // 4 seconds, in milliseconds
            }
        }

        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        user_number = pref.getString("Number","");

        String furl = "https://wifiap-1361.firebaseio.com/" + user_number;
        Log.d("furl", furl);

        user = new Firebase(furl);
        pinger = new Firebase(pinger_url);
        pinger.child("pinged").setValue("true");
        pinger.child("pinged_by").setValue(user_number);
        Log.d("pinger \n user", pinger.toString() + "\n" + user.toString());


        //new getdirectionresp().execute("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }

    @Override
    public void onBackPressed() {
        Log.d("back", "back");
        pinger.child("pinged").setValue("false");
        finish();
    }

    public boolean checkperm(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
        flocation = user.child("location");
        flocation.setValue(location);


        //Toast.makeText(getApplicationContext(),"polo2", Toast.LENGTH_SHORT).show();
        Log.d("locc", location.toString());

        double currentlat = location.getLatitude();
        double currentlong = location.getLongitude();

        if(marker != null){
            marker.remove();
        }

        LatLng latlong = new LatLng(currentlat, currentlong);
        MarkerOptions moptions = new MarkerOptions()
                .position(latlong);
        marker = mMap.addMarker(moptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlong)
                .tilt(60)
                .zoom(16)
                .build();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlong));
        if (!once){
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            once = true;
        }


        Location loctest = new Location("");
        loctest.setLatitude(26.1834051);
        loctest.setLongitude(91.78202229999999);

        getPingerLocation(location);

        //##################################### Drawwing poly too
        new getdirectionresp().execute("");

        //distance = CalculationByDistance(latlong,ll);
        //Toast.makeText(getApplicationContext(), "::" + distance, Toast.LENGTH_SHORT).show();

//        if (testtoast != null){
//            testtoast.cancel();
//        }
//        testtoast = Toast.makeText(getApplicationContext(),"At: "+ latlong.toString() + "\n::" + distance,Toast.LENGTH_SHORT);
//        testtoast.show();


    }

    public void getPingerLocation(final Location loc){

        Log.d("plococ", "##");
        pinger.child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String lat, longi;
                lat = dataSnapshot.child("latitude").getValue().toString();
                longi = dataSnapshot.child("longitude").getValue().toString();

                pinger_location = new Location("");
                pinger_location.setLatitude(Double.parseDouble(lat));
                pinger_location.setLongitude(Double.parseDouble(longi));

                double la = pinger_location.getLatitude();
                double lo = pinger_location.getLongitude();
                LatLng ll = new LatLng(la,lo);

                /////
                double lao = loc.getLatitude();
                double loo = loc.getLongitude();
                LatLng ori = new LatLng(lao,loo);

                double dis = CalculationByDistance(ori, ll);
                Log.d("DIS", dis + "");

                if (dis < 0.1){
                    met = true;
                }

                if (dis >= 0.1 && met){
                    setnotif_fire();
                }

                if (testtoast != null){
                    testtoast.cancel();
                }
                testtoast = Toast.makeText(getApplicationContext(), "Distance :: " + dis, Toast.LENGTH_SHORT);
                testtoast.show();

                if(pinger_marker != null){
                    pinger_marker.remove();
                }

                MarkerOptions mopts = new MarkerOptions().position(ll);
                pinger_marker = mMap.addMarker(mopts);


                Log.d("lat", dataSnapshot.child("latitude").getValue().toString());

                Log.d("ploc", dataSnapshot.getValue().toString());
                test = build_query(loc, pinger_location);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setnotif_fire()
    {
        String furl = "https://wifiap-1361.firebaseio.com/" + user_number + "/notif";
        String furl_pinger = "https://wifiap-1361.firebaseio.com/" + pinger_num + "/notif";;

        notif = new Firebase(furl);
        notif_pinger = new Firebase(furl_pinger);

        notif.setValue("true");
        notif_pinger.setValue("true");
    }

    @Override
    public void onLocationChanged(Location location)
    {
        handleLocation(location);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {

        int Radius=6371;//radius of earth Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return Radius * c;
    }

    private class getdirectionresp extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpHandler httphandler = new HttpHandler();

            dirresponse = httphandler.GetHTTPData(test);

            return dirresponse;
        }

        @Override
        protected void onPostExecute(String dirresponse){

            if(dirresponse != null){
                try {
                    JSONObject root = new JSONObject(dirresponse);

                    JSONArray routes = root.getJSONArray("routes");
                    for(int i = 0; i < routes.length(); ++i)
                    {
                        JSONObject poly_j = routes.getJSONObject(i);
                        JSONObject point = poly_j.getJSONObject("overview_polyline");
                        poly_S = point.getString("points");

                        Log.d("poly",poly_S);

                        dpoly = decodePoly(poly_S);

                        mMap.addPolyline(new PolylineOptions().addAll(dpoly));
                        Log.d("\n\n::::::: ADDED :::::::", "POLY");

                        for (int j = 0; j < dpoly.size(); ++j){
                            Log.d("listp", dpoly.get(j).toString());
                        }


                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //TOAST
            }
        }
    }

    private java.util.List<LatLng> decodePoly(String encoded) {

        java.util.List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void setupmap(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.889, -87.622), 16));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        mMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(41.889, -87.622)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkperm()){
            mMap.setMyLocationEnabled(true);
        }

    }

    public String build_query(Location origin, Location dest){
        double origin_lat, origin_long, dest_lat, dest_long;
        StringBuilder query = new StringBuilder();
        query.append("http://maps.googleapis.com/maps/api/directions/json?origin=");

        origin_lat = origin.getLatitude();
        origin_long = origin.getLongitude();
        dest_lat = dest.getLatitude();
        dest_long = dest.getLongitude();

        query.append(origin_lat + ",");
        query.append(origin_long + "&destination=");
        query.append(dest_lat + ",");
        query.append(dest_long);

        Log.d("BUILD Q", query.toString());

        return query.toString();
    }
}
