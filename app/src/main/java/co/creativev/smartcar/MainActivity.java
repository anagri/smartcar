package co.creativev.smartcar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {
    private TextView textSpeed, textBattery, textEngineLoad, textCoolentTemp, textGas, textRPM, textOverview;
    private OBDService obdService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textSpeed = ((TextView) findViewById(R.id.textSpeed));
        textBattery = ((TextView) findViewById(R.id.textBattery));
        textCoolentTemp = ((TextView) findViewById(R.id.textCoolantTemp));
        textEngineLoad = ((TextView) findViewById(R.id.textEngineTemp));
        textGas = ((TextView) findViewById(R.id.textGas));
        textRPM = ((TextView) findViewById(R.id.textRPM));
        textOverview = ((TextView) findViewById(R.id.textOverview));
        obdService = new OBDServiceFile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (true) {
                    final OBDStats obdStats = obdService.fetch();
                    if (obdStats == null) break;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textSpeed.setText(format("%d km/hr", obdStats.speed));
                            textRPM.setText(format("%d rpm", obdStats.rpm));
                            textCoolentTemp.setText(format("%d C", obdStats.coolantTemp));
                            textEngineLoad.setText(format("%d %%", obdStats.engineLoad));
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                textOverview.setText("Finished");
            }
        }.execute();
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

}
