package kdas.i_nterface.uitest_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class Timeline_category extends AppCompatActivity {

    int day;

    CardView prof, friends, all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_timeline_category);


        Bundle bundle = getIntent().getExtras();
        day = bundle.getInt("day");

        prof = (CardView)findViewById(R.id.cat_1);
        friends = (CardView)findViewById(R.id.cat_2);
        all = (CardView)findViewById(R.id.cat_3);

        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Timeline_category.this, EventTimeline.class);
                i.putExtra("cat", 1);
                i.putExtra("day", day);
                startActivity(i);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Timeline_category.this, EventTimeline.class);
                i.putExtra("cat", 3);
                i.putExtra("day", day);
                startActivity(i);
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Timeline_category.this, EventTimeline.class);
                i.putExtra("cat", 3);
                i.putExtra("day", day);
                startActivity(i);
            }
        });


    }
}
