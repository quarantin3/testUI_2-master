package kdas.i_nterface.uitest_2;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class Main2Activity extends AppCompatActivity {

    Thread ref;
    Firebase notif, pinged, pinger;
    String user_num, pinger_num;

    boolean thread = false;
    boolean bnotif = false;
    boolean init = false;
    boolean chk_ping_once = false;


    FloatingActionButton fab1, fab2, fab3, fab4, fab_route_ping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Firebase.setAndroidContext(this);

        //android.support.v7.widget.Toolbar  toolbar = (android.support.v7.widget.Toolbar ) findViewById(R.id.main2toolbar);
        //setSupportActionBar(toolbar);

//        if (toolbar != null)
//            toolbar.setTitle("SOLO");
//        toolbar.setTitleTextColor(getResources().getColor(R.color.some_white));
//        setSupportActionBar(toolbar);

        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        user_num = pref.getString("Number","");
        init = pref.getBoolean("Init", init);
        Log.d("####", user_num);
        Log.d("usr", user_num);
        Log.d("init", init + "");

        if (!init){
            Intent i = new Intent(Main2Activity.this, Init.class);
            startActivity(i);
        }

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (!thread){
                    check_notif();
                    Log.d("Keep", "running");
                }
            }
        }, 0, 10, TimeUnit.SECONDS);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main_1);
//        if (fab != null){
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, List.class);
//                    startActivity(i);
//                }
//            });
//        }
//
//        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_main_2);
//        if (fab2 != null){
//            fab2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, Main3Activity.class);
//                    startActivity(i);
//                }
//            });
//        }
//
//        FloatingActionButton fab3 = (FloatingActionButton)findViewById(R.id.fab_main_3);
//        if(fab3 != null){
//            fab3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, MapsActivity.class);
//                    startActivity(i);
//                }
//            });
//        }
//
//        FloatingActionButton fab4 = (FloatingActionButton)findViewById(R.id.fab_main_4);
//        if(fab4 != null){
//            fab4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, Ping.class);
//                    startActivity(i);
//                    Log.d("done", "done");
//                }
//            });
//        }else {
//            Log.d("done", "done2");
//        }
//
//        FloatingActionButton fab5 = (FloatingActionButton)findViewById(R.id.fab_main_5);
//        if (fab5 != null){
//            fab5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, Init.class);
//                    startActivity(i);
//                }
//            });
//        }
//
//        FloatingActionButton fab6 = (FloatingActionButton)findViewById(R.id.fab_main_6);
//        if (fab6 != null){
//            fab6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i2 = new Intent(Main2Activity.this, share_location.class);
//                    startActivity(i2);
//
////                    Log.d("2","2");
//                }
//            });
//        }
//
//        FloatingActionButton fab_red = (FloatingActionButton)findViewById(R.id.fab_main_7);
//        if (fab_red != null){
//            fab_red.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(Main2Activity.this, EventTimeline.class);
//                    startActivity(i);
//                }
//            });
//        }


//        fab1 = (FloatingActionButton)findViewById(R.id.fab_init);
//        fab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(Main2Activity.this, Init.class);
//                startActivity(i);
//
//            }
//        });

        fab2 = (FloatingActionButton)findViewById(R.id.fab_share);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, share_location.class);
                startActivity(i);

            }
        });

        fab3 = (FloatingActionButton)findViewById(R.id.fab_events);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, Main3Activity.class);
                startActivity(i);

            }
        });

        fab4 = (FloatingActionButton)findViewById(R.id.fab_peers);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, Ping.class);
                startActivity(i);

            }
        });

        fab_route_ping = (FloatingActionButton)findViewById(R.id.gogo);
        fab_route_ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_pinged();
            }
        });


    }

    public void check_notif(){

        String furl = "https://wifiap-1361.firebaseio.com/" + user_num + "/notif";
        Log.d("ff", furl);
        notif = new Firebase(furl);
        notif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bnotif = dataSnapshot.getValue(boolean.class);
                if (bnotif) {
                    thread = true;
                    run_pug();
                    Log.d("Stop", "running");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void check_pinged(){
        String furl = "https://wifiap-1361.firebaseio.com/" + user_num + "/pinged";
        pinged = new Firebase(furl);
        Log.d("pinged", pinged.toString());
        if (!chk_ping_once){
            pinged.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean pinged = dataSnapshot.getValue(boolean.class);
                    if (pinged){
                        route_pinger();
                    }else{
                        Toast.makeText(getApplicationContext(), "Nobody has pinged you", Toast.LENGTH_LONG).show();
                        chk_ping_once = true;

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

    }

    public void route_pinger(){
        String furl_route = "https://wifiap-1361.firebaseio.com/" + user_num + "/pinged_by/";
        pinger = new Firebase(furl_route);
        Log.d("pinger", pinger.toString());
        pinger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pinger_num = dataSnapshot.getValue(String.class);

                if (!chk_ping_once){
                    Intent i = new Intent(Main2Activity.this, MapsActivity.class);
                    i.putExtra("pinger_num", pinger_num);
                    startActivity(i);
                }else {
                    chk_ping_once = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void run_pug(){
        PugNotification.with(getApplicationContext())
                .load()
                .title("Hey !")
                .message("You just met someone, Click!")
                .bigTextStyle("You just met someone, spare a moment buddy!")
                .smallIcon(R.drawable.nmsmall)
                .largeIcon(R.drawable.nmlarge)
                .flags(Notification.DEFAULT_ALL)
                .click(Note.class,null)
                .dismiss(Note.class,null)
                .simple()
                .build();

    }

}
