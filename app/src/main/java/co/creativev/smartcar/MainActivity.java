package co.creativev.smartcar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {
    private TextView textSpeed, textBattery, textEngineLoad, textCoolentTemp, textGas, textRPM, textOverview;
    private DataChangeListener listener;
    private Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://blazing-heat-9235.firebaseio.com/resp");
        setContentView(R.layout.activity_main);
        textSpeed = ((TextView) findViewById(R.id.textSpeed));
        textBattery = ((TextView) findViewById(R.id.textBattery));
        textCoolentTemp = ((TextView) findViewById(R.id.textCoolantTemp));
        textEngineLoad = ((TextView) findViewById(R.id.textEngineTemp));
        textGas = ((TextView) findViewById(R.id.textGas));
        textRPM = ((TextView) findViewById(R.id.textRPM));
        textOverview = ((TextView) findViewById(R.id.textOverview));
        listener = new DataChangeListener();
        firebase.addChildEventListener(listener);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("APPLOG", "Data changed " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DataChangeListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("APPLOG", "Child added " + dataSnapshot.getKey());
            updateUI(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d("APPLOG", "Child changed " + dataSnapshot.getKey());
            updateUI(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d("APPLOG", "Child removed " + dataSnapshot.getKey());
            updateUI(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            Log.d("APPLOG", "Child moved " + dataSnapshot.getKey());
            updateUI(dataSnapshot);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d("APPLOG", "Firebase error" + firebaseError.getMessage());
        }

        private void updateUI(DataSnapshot dataSnapshot) {
            try {
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue(String.class);
                switch (key) {
                    case "engine_coolent_temprature":
                        textCoolentTemp.setText(format("%s C", value));
                        break;
                    case "engine_load":
                        textEngineLoad.setText(format("%s %%", value));
                        break;
                    case "engine_rpm":
                        textRPM.setText(format("%s r/s", value));
                        break;
                    case "vehicle_speed":
                        textSpeed.setText(format("%s km/hr", value));
                        break;
                }
            } catch (Exception e) {
                Log.e("APPLOG", "Exception while updating values", e);
            }
        }
    }
}
