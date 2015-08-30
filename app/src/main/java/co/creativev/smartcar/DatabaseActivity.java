package co.creativev.smartcar;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DatabaseActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private EditText input;
    private ArrayList devices;
    private BluetoothSocket socket;
    private TextView textOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        input = (EditText) findViewById(R.id.textRawInput);
        textOutput = (TextView) findViewById(R.id.textRawOutput);

    }

    @Override
    protected void onStart() {
        super.onStart();
        findBluetoothDevices();
    }

    private void findBluetoothDevices() {
        ArrayList deviceStrs = new ArrayList();
        devices = new ArrayList();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (Object device : pairedDevices) {
                BluetoothDevice bdevice = (BluetoothDevice) device;
                deviceStrs.add(bdevice.getName() + "\n" + bdevice.getAddress());
                devices.add(bdevice.getAddress());
            }
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));
        alertDialog.setSingleChoiceItems(adapter, -1, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_database, menu);
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
            findBluetoothDevices();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submit(View v) {
        String command = input.getText().toString();
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                String command = params[0];
                try {
                    ObdCommand obdCommand = new ObdCommand(command);
                    obdCommand.run(socket.getInputStream(), socket.getOutputStream());
                    return obdCommand.getRawData();
                } catch (Exception e) {
                    Log.e("APPLOG", "Network connection exception", e);
                    return "ERROR " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.startsWith("ERROR")) {
                    Toast.makeText(DatabaseActivity.this, s, Toast.LENGTH_SHORT).show();
                    textOutput.setText(s);
                } else {
                    textOutput.setText("Error");
                }
            }
        }.execute(command);
    }

    public void fuelPressure(View v) {
        input.setText("01 0A");
    }

    public void biometricPressure(View v) {
        input.setText("01 33");
    }

    public void fuelRailPressure(View v) {
        input.setText("01 23");
    }

    public void intakeManifoldPressure(View v) {
        input.setText("01 0B");
    }

    public void airIntakeTemp(View v) {
        input.setText("01 0F");
    }

    public void ambientAirTemperature(View v) {
        input.setText("01 46");
    }

    public void engineCoolantTemperature(View v) {
        input.setText("01 05");
    }

    public void oilTemp(View v) {
        input.setText("01 5C");
    }

    public void speed(View v) {
        input.setText("01 0D");
    }

    public void rpm(View v) {
        input.setText("01 0C");
    }

    public void airFuelRatio(View v) {
        input.setText("01 44");
    }

    public void distanceTraveled(View v) {
        input.setText("01 31");
    }


    public void troubleCodes(View v) {
        input.setText("03");
    }

    public void echo(View v) {
        input.setText("AT E0");
    }

    public void lineFeed(View v) {
        input.setText("AT L0");
    }

    public void timeout(View v) {
        input.setText("AT ST " + Integer.toHexString(0xFF & 10));
    }

    public void auto(View v) {
        input.setText("AT SP 0");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
        String deviceAddress = devices.get(position).toString();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//AA:BB:CC:11:22:33");
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            Log.e("APPLOG", "Bluetooth connection exception", e);
            Toast.makeText(this, "Bluetooth connection exception", Toast.LENGTH_SHORT).show();
        }
    }
}
