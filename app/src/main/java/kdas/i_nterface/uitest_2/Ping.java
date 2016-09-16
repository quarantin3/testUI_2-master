package kdas.i_nterface.uitest_2;

import android.os.Bundle;
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

public class Ping extends AppCompatActivity {

    com.bhargavms.dotloader.DotLoader loader, loader2;

    Firebase peers, peers_name;

    java.util.List<String> con_num = new ArrayList<>();
    java.util.List<String> con_name = new ArrayList<>();
    java.util.List<Contacts> con = new ArrayList<>();

    Contact_adapter_ping adapter;

    Thread peer;
    boolean con_name_done = false;
    boolean con_num_done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        getSupportActionBar().hide();

        RecyclerView rv = (RecyclerView)findViewById(R.id.view_rv_ping);

        adapter = new Contact_adapter_ping(this, con);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        loader = (com.bhargavms.dotloader.DotLoader)findViewById(R.id.text_dot_loader_ping);
        loader2 = (com.bhargavms.dotloader.DotLoader)findViewById(R.id.text_dot_loader_toolbar_ping_2);

        loader2.setVisibility(View.INVISIBLE);

        Firebase.setAndroidContext(this);
        peers = new Firebase("https://wifiap-1361.firebaseio.com/peers");
        peers_name = new Firebase("https://wifiap-1361.firebaseio.com/peers_name");

//        for (int i = 0; i < 6; ++i) {
//            con_name.add("Kabir Das");
//        }
//        Log.d("CON", con_name.toString());
//        peers.setValue(con_num);

        //################## SP test
//        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
//        Log.d("pref", pref.getString("Number",""));

            peers_name.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<java.util.List<String>> t = new GenericTypeIndicator<java.util.List<String>>() {
                    };
                    con_name = dataSnapshot.getValue(t);
                    con_name_done = true;
                    peer();
                    Log.d("name_done", con_name_done + "\n" + con_name.toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


    }//####################

    public void peer(){
        if (con_name_done) {
            peers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<java.util.List<String>> t = new GenericTypeIndicator<java.util.List<String>>() {
                    };
                    con_num = dataSnapshot.getValue(t);

                    loader.setVisibility(View.INVISIBLE);
                    loader2.setVisibility(View.VISIBLE);

                    for (int i = 0; i < con_num.size(); ++i) {
                        con.add(new Contacts(con_name.get(i), con_num.get(i)));
                    }
                    adapter.notifyDataSetChanged();

                    Log.d(":: ", con_num.toString());
                    con_num_done = true;

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }



}
