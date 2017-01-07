package com.wichita.overwatch.overwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class BluetoothSerialCommunication extends AppCompatActivity {

    TextView myLabel;
    EditText myTextbox;

    /*
    * Service Connection Step 05 - 08:
    * Service Connection Step 05:
    * create a service object to connect to
    * create a test variable to determine if the activity has been bound to the service
    * */
    static BluetoothConnectionService bluetoothConnectionServiceBSC;
    boolean isBound = false;
    /*
    * Service Connection Step 06:
    * create a connection
    * */
    private ServiceConnection bluetoothConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*
            * Service Connection Step 07:
            * on connect do this....
            * A:    create a binder
            * B:    bind the service
            * C:    set the test variable for boundness to true
            * */
            BluetoothConnectionService.BluetoothConnectionServiceBinder binder = (BluetoothConnectionService.BluetoothConnectionServiceBinder) service;
            bluetoothConnectionServiceBSC = BluetoothSetup.bluetoothConnectionService01;
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*
            * Service Connection Step 08:
            * on disconnect do this
            * A:    set the  test variable for boundedness to false
            * */
            isBound = false;

        }
    };
    //END Service Connection Step 05 - 08:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_serial_communication);

        Button sendButton = (Button)findViewById(R.id.send);

        myLabel = (TextView)findViewById(R.id.textView01);
        myTextbox = (EditText)findViewById(R.id.entry);

        /*
        * Service Connection Step 09:
        * A:    create intent to bind
        * B:    bind the intent, connection, context
        * */
        Intent intent01 = new Intent(this, BluetoothConnectionService.class);
        bindService(intent01, bluetoothConnection, Context.BIND_AUTO_CREATE);
        //Service Connection END Step 09:

        //SEND button's click listener: Initialization, Construction, Method
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String msg = myTextbox.getText().toString();
                    msg += "\n";
                    bluetoothConnectionServiceBSC.sendDataOverBluetooth(msg);
                    showMessage("Sending: " + msg);
                    myLabel.setText("Data Sent");
                } catch (IOException ex) {
                    showMessage("SEND FAILED");
                } catch (Exception e) {
                    showMessage("sendButton.setOnClickListener() E ERROR");
                }
            }
        });
    }

    //Method which displays a "toast" on the Android device: (Black Message Box)
    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_serial_communication, menu);
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
}//End BluetoothSerialCommunication.java