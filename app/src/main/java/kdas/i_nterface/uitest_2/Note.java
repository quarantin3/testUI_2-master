package kdas.i_nterface.uitest_2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

public class Note extends AppCompatActivity {

    String note, user_number, furl, furl2, furlpoint, furlnotif;
    int ipoints;
    Firebase gist_note, note2, points, notif_reset;

    boolean done1 = false;
    boolean done2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().hide();
        Firebase.setAndroidContext(this);



        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        user_number = pref.getString("Number","");

        final EditText note_ed = (EditText)findViewById(R.id.note_ed);

        FloatingActionButton note_done = (FloatingActionButton)findViewById(R.id.note_done);
        note_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note = note_ed.getText().toString();

                Date da = new Date();
                final int furlx = getposition_from_time(da);

                furl = "https://wifiap-1361.firebaseio.com/" + user_number + "/data/" + furlx + "/gist_note";
                furlpoint = "https://wifiap-1361.firebaseio.com/" + user_number + "/data/" + furlx + "/points";
                furlnotif = "https://wifiap-1361.firebaseio.com/" + user_number + "/notif";
                Log.d("not", furlnotif);

                gist_note = new Firebase(furl);
                points = new Firebase(furlpoint);
                notif_reset = new Firebase(furlnotif);

                //gist_note.setValue(note);

                done2 = true;

                points.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ipoints = dataSnapshot.getValue(int.class);
                        ipoints -= 1;
                        furl2 = "https://wifiap-1361.firebaseio.com/" + user_number + "/data/" + furlx + "/points_data/" + ipoints +"/notes";
                        Log.d("furl2", furl2);
                        note2 = new Firebase(furl2);
                        note2.setValue(note);
                        done1 = true;

                        notif_reset.setValue("false");
                        finish();

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        });




    }

    public int getposition_from_time(Date date){

        int pos = 0;
        for (long i = 1451586600000L; i < date.getTime(); i += 86400000){
            ++pos;
        }
        return pos;
    }
}
