package com.wichita.overwatch.overwatch;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button bluetoothSerialCommunicationSwitchScreens;
    Button bluetoothRemoteControlSwitchScreens;
    Button plotRouteSwitchScreens;
    Button bluetoothSetupSwitchScreens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothSetupSwitchScreens = (Button)findViewById(R.id.bluetoothSetup);
        bluetoothSetupSwitchScreens.setOnClickListener(this);
        bluetoothSerialCommunicationSwitchScreens = (Button)findViewById(R.id.bluetoothSerialCommunication);
        bluetoothSerialCommunicationSwitchScreens.setOnClickListener(this);
        bluetoothRemoteControlSwitchScreens = (Button)findViewById(R.id.bluetoothRemoteControl);
        bluetoothRemoteControlSwitchScreens.setOnClickListener(this);
        plotRouteSwitchScreens = (Button)findViewById(R.id.plotRoute);
        plotRouteSwitchScreens.setOnClickListener(this);
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

    //Method which prints a message in the form of a toast (Black Message Box) to the screen
    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }

    //Method which creates the link to and start the Bluetooth Setup screen/activity
    public void bluetoothSetupSwitchScreensClick() throws Exception {
        Intent bluetoothSetupIntent = new Intent(MainActivity.this, BluetoothSetup.class);
        startActivity(bluetoothSetupIntent);
    }
    //Method which creates the link to and start the Bluetooth Serial Communication screen/activity
    public void bluetoothSerialCommunicationSwitchScreensClick() throws Exception{
        Intent bluetoothSerialCommunicationIntent = new Intent(MainActivity.this, BluetoothSerialCommunication.class);
        startActivity(bluetoothSerialCommunicationIntent);
    }
    //Method which creates the link to and start the Bluetooth Remote Control screen/activity
    public void bluetoothRemoteControlSwitchScreensClick() throws Exception{
        Intent bluetoothRemoteControlIntent = new Intent(MainActivity.this, BluetoothRemoteControl.class);
        startActivity(bluetoothRemoteControlIntent);
    }
    //Method which creates the link to and start the Bluetooth Remote Control screen/activity
    public void plotRouteSwitchScreensClick() throws Exception{
        try {
            Intent plotRouteIntent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(plotRouteIntent);
        }
        catch (Fragment.InstantiationException e) {
            showMessage("Error creating MapsActivity");
        }
        catch (Exception e) {
            showMessage("Error creating MapsActivity EXCEPTION e");
        }
    }

    //Chooses the appropriate onClick method to execute depending on which button has been clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetoothSetup:
                try {
                    bluetoothSetupSwitchScreensClick();
                    break;
                } catch (Exception e) {
                    showMessage("bluetoothSetupSwitchScreensClick() E ERROR");
                }
            case R.id.bluetoothSerialCommunication:
                try {
                    bluetoothSerialCommunicationSwitchScreensClick();
                    break;
                } catch (Exception e) {
                    showMessage("bluetoothSerialCommunicationsSwi...() E ERROR");
                }
            case R.id.bluetoothRemoteControl:
                try {
                    bluetoothRemoteControlSwitchScreensClick();
                    break;
                } catch (Exception e) {
                    showMessage("bluetoothSerialCommunicationsSwi...() E ERROR");
                }
            case R.id.plotRoute:
                try {
                    plotRouteSwitchScreensClick();
                    break;
                } catch (Exception e) {
                    showMessage("plotRouteSwitchScreensClick() E ERROR");
                }
            default:
                showMessage("onClick(View v){ switch(v.getId()) }  default ERROR");
                break;
        }
    }

}//End MainActivity.java