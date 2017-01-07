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
import android.widget.Toast;

public class BluetoothRemoteControl extends AppCompatActivity {

    /*
    * Step 05:
    * create a service object to connect to
    * create a test variable to determine if the activity has been bound to the service
    * */
    static BluetoothConnectionService bluetoothConnectionServiceBRC;
    boolean isBound = false;

    /*
    * Step 06:
    * create a connection
    * */
    private ServiceConnection bluetoothConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*
            * Step 07:
            * on connect do this....
            * A:    create a binder
            * B:    bind the service
            * C:    set the test variable for boundness to true
            * */
            //BluetoothConnectionServiceBinder binder = (BluetoothConnectionServiceBinder) service;
            bluetoothConnectionServiceBRC = BluetoothSetup.bluetoothConnectionService01;
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*
            * Step 08:
            * on disconnect do this
            * A:    set the  test variable for boundedness to false
            * */
            isBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_remote_control);

        Button forward = (Button)findViewById(R.id.forward);
        Button backward = (Button)findViewById(R.id.backward);
        Button left = (Button)findViewById(R.id.left);
        Button right = (Button)findViewById(R.id.right);
        Button a = (Button)findViewById(R.id.a);
        Button b = (Button)findViewById(R.id.b);
        Button select = (Button)findViewById(R.id.select);
        Button start = (Button)findViewById(R.id.start);

        /*
        * Step 09:
        * A:    create intent to bind
        * B:    bind the intent, connection, context
        * */
        Intent intent01 = new Intent(this, BluetoothConnectionService.class);
        bindService(intent01, bluetoothConnection, Context.BIND_AUTO_CREATE);
        //END Step 09:

        /*
        * Each of the forward, backward, left, and right buttons have remote control style controls
        * On pressing button the action command is sent ex) forward
        * On releasing button the halt command is sent ex) notforward
        * When it is released the reset type command is given
        */
        //onClickListeners for Forward button
        forward.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~notforward");
                        } catch (Exception e) {
                            showMessage("forward.setOnClickListener() E ERROR");
                        }
                    }
                }
        );
        forward.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~forward");
                        } catch (Exception e) {
                            showMessage("forward.setOnLongClickListener() E ERROR");
                        }
                        return false;
                    }
                }
        );
        /*//Only used when repeating signals need sent
        forward.setOnTouchListener(
                new RepeatListener(initialInterval, normalInterval, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            sendControllerSignal("~forward");
                        } catch (IOException ex) {
                            ;
                        }
                    }
                }));
        */

        //onClickListeners for Backward button
        backward.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~notbackward");
                        } catch (Exception e) {
                            showMessage("backward.setOnClickListener() E ERROR");
                        }
                    }
                }
        );
        backward.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~backward");
                        } catch (Exception e) {
                            showMessage("backward.setOnLongClickListener() E ERROR");
                        }
                        return false;
                    }
                }
        );
        /*
        //Only used when repeating signals need to be sent
        backward.setOnTouchListener(
                new RepeatListener(initialInterval, normalInterval, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            sendControllerSignal("~backward");
                        } catch (IOException ex) {
                            ;
                        }
                    }
                }));
        */

        //onClickListeners for Left button
        left.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~notleft");
                        } catch (Exception e) {
                            showMessage("left.setOnClickListener() E ERROR");
                        }
                    }
                }
        );
        left.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~left");
                        } catch (Exception e) {
                            showMessage("left.setOnLongClickListener() E ERROR");
                        }
                        return false;
                    }
                }
        );
        /*
        //Only used when repeating signals need to be sent
        left.setOnTouchListener(
                //RepeatListener(int initialInterval, int normalInterval, OnClickListener clickListener)
                new RepeatListener(initialInterval, normalInterval, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            sendControllerSignal("~left");
                        } catch (IOException ex) {
                            ;
                        }
                    }
                }));
        */

        //onClickListeners for Right button
        right.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~notright");
                        }
                        catch (Exception e) {
                            showMessage("right.setOnClickListener() E ERROR");
                        }
                    }
                }
        );
        right.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~right");
                        } catch (Exception e) {
                            showMessage("right.setOnLongClickListener() E ERROR");
                        }
                        return false;
                    }
                }
        );
        /*
        //Only used when repeating signals need to be sent
        right.setOnTouchListener(
                new RepeatListener(initialInterval, normalInterval, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            sendControllerSignal("~right");
                        } catch (IOException ex) {
                            ;
                        }
                    }
                }));
        */

        //onClickListener for A button
        a.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~a");
                        } catch (Exception e) {
                            showMessage("a.setOnClickListener() E ERROR");
                        }
                    }
                }
        );

        //onClickListener for B button
        b.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~b");
                        } catch (Exception e) {
                            showMessage("b.setOnClickListener() E ERROR");
                        }
                    }
                }
        );

        //onClickListener for Select button
        select.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~select");
                        }
                        catch (Exception e) {
                            showMessage("select.setOnClickListener() E ERROR");
                        }
                    }
                }
        );

        //onClickListener for Start button
        start.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            bluetoothConnectionServiceBRC.sendDataOverBluetooth("~start");
                        }
                        catch (Exception e) {
                            showMessage("right.setOnClickListener() E ERROR");
                        }
                    }
                }
        );

    }//END onCreate

    //Prints a message to the Android screen (in the form of a toast: black message box)
    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_remote_control, menu);
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

}//END BluetoothRemoteControl.java