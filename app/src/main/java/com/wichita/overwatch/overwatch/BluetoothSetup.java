package com.wichita.overwatch.overwatch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
* Service Connection Step 04:
* import the BluetoothConnectionService and the BluetoothConnectionServiceBinder
* */
import com.wichita.overwatch.overwatch.BluetoothConnectionService.BluetoothConnectionServiceBinder;

public class BluetoothSetup extends AppCompatActivity {

    TextView bSJTextView01;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    //Debugging
    private static final String TAG = "Bluetooth";
    private static final boolean D = true;

    boolean getname = false;

    /*
    * Service Connection Step 05:
    * create a service object to connect to
    * create a test variable to determine if the activity has been bound to the service
    * */
    static BluetoothConnectionService bluetoothConnectionService01;
    boolean isBound = false;
    //END  Service Connection Step 05:

    /*
    * Service Connection Step 06 - 08:
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
            BluetoothConnectionServiceBinder binder = (BluetoothConnectionServiceBinder) service;
            bluetoothConnectionService01 = binder.getService();
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

    /*
    * Service Connection Step 10:
    * A:    create methods that the BluetoothSetup will use
    *       ex) public void serviceMethod(View view) //pass the views for the UI changes
    * B:    use bluetoothConnectionServiceLocalVariable.someMethod() to use the service
    *   Reasoning:
    *   Do UI modification in Activity while the service continues to run its threads. All
    *   activities bound to this service will keep the Bluetooth connection alive and accessible to
    *   the application.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setup);
        /*
        * Service Connection Step 09:
        * A:    create intent to bind
        * B:    bind the intent, connection, context
        * */
        Intent intent01 = new Intent(this, BluetoothConnectionService.class);
        bindService(intent01, bluetoothConnection, Context.BIND_AUTO_CREATE);
        //END Service Connection Step 09:

        bSJTextView01 = (TextView)findViewById(R.id.bSXTextView01);
        Button bSJOpenButton = (Button)findViewById(R.id.bSXOpenButton);
        Button bSJCloseButton = (Button)findViewById(R.id.bSXCloseButton);
        Button bSJDiscoverButton = (Button)findViewById(R.id.bsXDiscoverButton);

        bluetoothConnectionService01.mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        //bSJDiscover button's click listener: Initialization, Construction, Method
        bSJDiscoverButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        showMessage("Bluetooth is not available");
                        finish();
                        return;
                    } else if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }
                    Intent serverIntent = new Intent(BluetoothSetup.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } catch (Exception e) {
                    showMessage("bSJDiscoverButton.setOnClickListener() E ERROR");
                }
            }
        });//END bSJDiscover onClickListener

        //bSJOPEN button's click listener: Initialization, Construction, Method
        bSJOpenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isConnected;
                try {

                    if (mBluetoothAdapter == null) {
                        showMessage("No bluetooth adapter available");
                    } else if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        bSJTextView01.setText("After Enabling Bluetooth\nTry Open Again");
                        return;
                    }
                    isConnected = bluetoothConnectionService01.openBT();
                    if (isConnected) {
                        bSJTextView01.setText(
                                            "Bluetooth Opened" +
                                            "\nYour Device:\t" + bluetoothConnectionService01.mBluetoothAdapter.getName() +
                                            "\nYour MAC:\t" + bluetoothConnectionService01.mBluetoothAdapter.getAddress() +
                                            "\nConnected Device:\t" + bluetoothConnectionService01.mmDevice.getName() +
                                            "\nConnected MAC:\t" + bluetoothConnectionService01.mmDevice.getAddress()
                                             );
                    }
                } catch (Exception e) {
                    showMessage("openButton.setOnClickListener() E ERROR");
                }
            }
        });

        //bSJCLOSE button's click listener: Initialization, Construction, Method
        bSJCloseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!mBluetoothAdapter.isEnabled()){
                        showMessage("Bluetooth is not on");
                        return;
                    }
                    bluetoothConnectionService01.closeBT();
                    bSJTextView01.setText("Bluetooth Closed");
                } catch (Exception e) {
                    showMessage("closeButton.setOnClickListener() E ERROR");
                }
                //Turn off Bluetooth Adapter on Bluetooth
                try {
                    bluetoothConnectionService01.mBluetoothAdapter.disable();
                    bSJTextView01.setText("Bluetooth Off");
                } catch (Exception e) {
                    showMessage("bluetoothConnectionService01.mBluetoothAdapter.disable() E ERROR");
                }
            }
        });

    }//END onCreate

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    getname=false;
                    bSJTextView01.setText(
                            "Selected Device" +
                            "\nMAC Address:\t" + data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
                                connectDevice(data);
                }
                break;
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        bluetoothConnectionService01.mmDevice = bluetoothConnectionService01.mBluetoothAdapter.getRemoteDevice(address);

    }

    //Method which prints messages to the screen called "toasts" (the black box message screens)
    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_setup, menu);
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

}//End BluetoothSetup.java