package com.wichita.overwatch.overwatch;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Binder;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/*
* Service Use Instructions:
* ------------------------
* Use Service Connection Steps 4 - 10 shown in BluetoothSetup Activity
* On Service Connection Step 5, name the BluetoothConnectionService something locally appropriate
* On Service Connection Step 10, use step 05's connection variable when the connection is needed
*/
public class BluetoothConnectionService extends Service {

    /*
    * Service Connection Step 01:
    * create the binder or bridge between client and service
    * */
    private final IBinder bluetoothConnectionServiceBinder = new BluetoothConnectionServiceBinder();

    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    static Thread workerThread;
    static byte[] readBuffer;
    static int readBufferPosition;
    static volatile boolean stopWorker;
    static String storeIncomingData;

    public static boolean isStopWorker() {
        return stopWorker;
    }

    public static void setStopWorker(boolean stopWorker) {
        BluetoothConnectionService.stopWorker = stopWorker;
    }

    public static int getReadBufferPosition() {
        return readBufferPosition;
    }

    public static void setReadBufferPosition(int readBufferPosition) {
        BluetoothConnectionService.readBufferPosition = readBufferPosition;
    }

    public static byte[] getReadBuffer() {
        return readBuffer;
    }

    public static void setReadBuffer(byte[] readBuffer) {
        BluetoothConnectionService.readBuffer = readBuffer;
    }

    public static Thread getWorkerThread() {
        return workerThread;
    }

    public static void setWorkerThread(Thread workerThread) {
        BluetoothConnectionService.workerThread = workerThread;
    }

    public static InputStream getMmInputStream() {
        return mmInputStream;
    }

    public static void setMmInputStream(InputStream mmInputStream) {
        BluetoothConnectionService.mmInputStream = mmInputStream;
    }

    public static OutputStream getMmOutputStream() {
        return mmOutputStream;
    }

    public static void setMmOutputStream(OutputStream mmOutputStream) {
        BluetoothConnectionService.mmOutputStream = mmOutputStream;
    }

    public static BluetoothDevice getMmDevice() {
        return mmDevice;
    }

    public static void setMmDevice(BluetoothDevice mmDevice) {
        BluetoothConnectionService.mmDevice = mmDevice;
    }

    public static BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    public static void setMmSocket(BluetoothSocket mmSocket) {
        BluetoothConnectionService.mmSocket = mmSocket;
    }

    public static BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public static void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        BluetoothConnectionService.mBluetoothAdapter = mBluetoothAdapter;
    }

    //Constructors
    public BluetoothConnectionService() {
    }

    /*
    * Service Connection Step 11:
    * create the methods for this service called by the activity
    */

    /*
    * Method which listens for, receives, and stores messages from the UAD over bluetooth.
    * Slightly modified version available on Android Development Bluetooth page
    */
    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        setStopWorker(false);
        setReadBufferPosition(0);
        setReadBuffer(new byte [9999]);

        setWorkerThread(new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");

                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            storeIncomingData = data;
                                            if (storeIncomingData.equals("~echo")) {
                                                try {
                                                    sendDataOverBluetooth("~echoreply");
                                                }
                                                catch (Exception e) {
                                                    showMessage("Error beginListenForData()\n" +
                                                                "~echoreply not sent");
                                                }
                                            }
                                        }//END public void run()
                                    });//END handler.post(new Runnable()
                                }//END if(b == delimiter)
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }//END for(int i=0;i<bytesAvailable;i++)
                        }//END if(bytesAvailable > 0)
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }//END while(!Thread.currentThread().isInterrupted() && !stopWorker)
            }//END run()
        }));//End initialization and construction of workerThread

        //Start the thread workerThread
        workerThread.start();
    }
    //Method which sends a properly formatted string to the bluetooth "listener" thread
    void sendDataOverBluetooth(String str) throws IOException{
        str += "\n";
        mmOutputStream.write(str.getBytes());
    }

    //Method which connects the previously paired Bluetooth device to the Android device
    boolean openBT() throws IOException {
        //UUID with Bluetooth Serial Prefix indicating a connection with a serial Bluetooth device
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            return true;
        }
        catch (Exception connectionException) {
            //unable to connect ; close socket
            showMessage("Failed connection attempt");
            mmSocket.close();
            return false;
        }
    }

    //Method which attempts to close the connection to the connected Bluetooth device
    void closeBT() throws Exception {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    //Print to screen a message (black box message)
    public void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        /*
        * Service Connection Step 03
        * when onBind is called elsewhere this method returns the binder which is the class
        * created below
        * */
        return bluetoothConnectionServiceBinder;
    }

    /*
    * Service Connection Step 02:
    * create a class that extends the Binder creating the bridge above
    * */
    public class BluetoothConnectionServiceBinder extends Binder {
        BluetoothConnectionService getService() {
            return BluetoothConnectionService.this;
        }
    }

}//END BluetoothConnectionService.java