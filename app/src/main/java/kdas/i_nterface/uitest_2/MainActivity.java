package kdas.i_nterface.uitest_2;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> contacts = new ArrayList<String>();
    Cursor cursor;
    Thread readc;

    boolean thread_kill = false, read_done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        readc = new Thread(){
//            public void run(){
//                while (!thread_kill){
//                    try {
//                        readContacts();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                Log.d("thread", "finished");
//            }
//        };
//        readc.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                readContacts();
            }
        }).start();

        if (read_done)
            Toast.makeText(getApplicationContext(), "ffd" + contacts.size(), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "ffd", Toast.LENGTH_LONG).show();

    }

    public void size(ArrayList<String> temp){
        Toast.makeText(getApplicationContext(), "ffd" + temp.size(), Toast.LENGTH_LONG).show();
    }

    public void readContacts(){
        String phnum = null;

        Uri content_uri = ContactsContract.Contacts.CONTENT_URI;
        String id = ContactsContract.Contacts._ID;
        String display_name = ContactsContract.Contacts.DISPLAY_NAME;
        String hasphnum = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri phone_content_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String phone_contact_id = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String number = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(content_uri, null, null, null, null);

        if(cursor.getCount() > 0){
            int counter = 0;
            while(cursor.moveToNext()){
                output = new StringBuffer();
                String contact_id = cursor.getString(cursor.getColumnIndex(id));
                String name = cursor.getString(cursor.getColumnIndex(display_name));

                //Boolean hasnum = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(hasphnum)));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(hasphnum)));
                if(hasPhoneNumber > 0){
                    output.append("\nName ::" + name);

                    Cursor phcursor = contentResolver.query(phone_content_uri, null, phone_contact_id + " = ?", new String[] {contact_id}, null);
                    while (phcursor.moveToNext()){
                        phnum = phcursor.getString(phcursor.getColumnIndex(number));
                        output.append("\nNumber " + phnum);
                    }
                    phcursor.close();
                }

                contacts.add(output.toString());
                Log.d("on", "on");
            }
            Log.d("size :: ", "dsdsds");
            writef(contacts);

//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Hello, from the thread", Toast.LENGTH_LONG).show();
//                }
//            });

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Hello, from the thread ++ " + contacts.size(), Toast.LENGTH_LONG).show();

                }
            });



            read_done = true;
            thread_kill = true;


        }else {

        }
    }

    private void writef(ArrayList<String> data){
        try{
            File root = new File(Environment.getExternalStorageDirectory() + "/con.txt");
            root.createNewFile();

            FileOutputStream fout = new FileOutputStream(root);
            fout.write(data.toString().getBytes());
            fout.close();
            Log.d("file", "file");
//            Toast.makeText(getApplicationContext(), "ffd", Toast.LENGTH_LONG).show();


        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
