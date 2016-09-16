package kdas.i_nterface.uitest_2;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventTimeline extends AppCompatActivity {


    RecyclerView timeline;
    List<timeline_day_data> data = new ArrayList<>();

    java.util.List<events> mevents = new ArrayList<>();
    java.util.List<String> count = new ArrayList<>();
    List<List<String>> point_list = new ArrayList<java.util.List<String>>();
    List<String> point_data_list = new ArrayList<>();
    List<String> check_ins = new ArrayList<>();

    test_timeline_adpater adapater;

    int day, points, c = 0, cat;
    String user_number;
    String furl_it;
    String check_in_count, start_time, end_time, notes;
    Location point_location;

    Firebase ffurl, ffurl_it, ffurl_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_timeline);
        getSupportActionBar().hide();
        Firebase.setAndroidContext(this);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        // The View with the BottomSheetBehavior
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.d("onStateChanged", "onStateChanged:" + newState);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //fab.setVisibility(View.GONE);
                } else {
                    //fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                //Log.d("onSlide", "onSlide");
            }
        });
        behavior.setPeekHeight(120);

        Bundle bundle = getIntent().getExtras();
        day = bundle.getInt("day");
        cat = bundle.getInt("cat");
        Log.d("Day ::", day + "");

        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        user_number = pref.getString("Number", "");

        String furl = "https://wifiap-1361.firebaseio.com/" + user_number + "/data/" + day;

        switch (cat){
            case 1 : {
                furl_it = furl + "/points_data/professional/";
                break;
            }
            case 2 : {
                furl_it = furl + "/points_data/friends/";
                break;
            }
            case 3 : {
                furl_it = furl + "/points_data/all/";
                break;
            }
        }

        String furl_points = furl + "/points/";
        Log.d("FURL", furl + "\n" + furl_points);

        ffurl = new Firebase(furl);
        ffurl_points = new Firebase(furl_points);

        ffurl_points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                points = dataSnapshot.getValue(int.class);
                Log.d("points", points + "");
                point();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        timeline = (RecyclerView)findViewById(R.id.botttom_sheet_rv);

//        for (int i = 0; i < 3; ++i){
//            count.add(i, "true");
//        }
//        for (int i = 0; i < 20; ++i){
//            mevents.add(new events(count));
//        }

        adapater = new test_timeline_adpater(this, data);
        timeline.setAdapter(adapater);
        timeline.setLayoutManager(new LinearLayoutManager(this));
        adapater.notifyDataSetChanged();

    }

    public void point(){

        boolean done = false;
        for (int i = 0; i < points; ++i){
            String temp = furl_it + i;
            Log.d("temp", temp);
            ffurl_it = new Firebase(temp);
            Log.d("furl_it", ffurl_it+ "  " + i);
            Log.d("1","1");
            ffurl_it.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("1","2");
                    check_in_count = dataSnapshot.child("check_in_count").getValue(String.class);
                    start_time = dataSnapshot.child("start_time").getValue(String.class);
                    end_time = dataSnapshot.child("end_time").getValue(String.class);
                    notes = dataSnapshot.child("notes").getValue(String.class);

                    String lat, longi;

                    if (dataSnapshot.child("location").getValue(String.class).equals("#")){
                        lat = "21";
                        longi = "91";
                    }else {

                        lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                        longi = dataSnapshot.child("location").child("longitude").getValue().toString();
                    }


                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<java.util.List<String>>() {};
                    check_ins = dataSnapshot.child("check_ins").getValue(t);

                    point_location = new Location("");
                    point_location.setLatitude(Double.parseDouble(lat));
                    point_location.setLongitude(Double.parseDouble(longi));

                    point_data_list.add(check_in_count);
                    point_data_list.add(start_time);
                    point_data_list.add(end_time);
                    point_data_list.add(notes);

                    point_list.add(point_data_list);


                    /**
                     *
                     *
                     * point_list has data :: check-in count, starttime, endtime, notes
                     *
                     * location has well :: point locations
                     *
                     * checkins has data :: check in locations ////// NOT USED RN, Location object isnt managed for chkin in the backend yet
                     *
                     *
                     *
                     */
                    data.add(new timeline_day_data(point_data_list, check_ins, point_location));
                    adapater.notifyDataSetChanged();


                    Log.d("data single", point_data_list.toString());
                    Log.d("::::::: ", check_in_count + start_time + end_time + notes + point_location + check_ins + point_list);
                    Log.d("c", point_list.get(c).toString());
                    Log.d("size", point_list.size() + "");
                    ++c;
                    point_data_list.clear();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            done = true;

        }
    }
}
