package com.wichita.overwatch.overwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
* Service Connection Step 04:
* import the BluetoothConnectionService and the BluetoothConnectionServiceBinder
*/
import com.wichita.overwatch.overwatch.BluetoothConnectionService.BluetoothConnectionServiceBinder;

public class MapsActivity extends FragmentActivity {

    static final String STATE_USER = "user";
    private String saveStr;

    private GoogleMap map;
    static ArrayList<LatLng> markerPoints;
    EditText latlngStrings;

    /*
    * Service Connection Step 05:
    * create a service object to connect to
    * create a test variable to determine if the activity has been bound to the service
    */
    static BluetoothConnectionService bluetoothConnectionServiceGMA;
    boolean isBound = false;
    //END  Service Connection Step 05:

    /*
    * Service Connection Step 06 - 08:
    * Service Connection Step 06:
    * create a connection
    */
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
            bluetoothConnectionServiceGMA = BluetoothSetup.bluetoothConnectionService01;
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*
            * Service Connection Step 08:
            * on disconnect do this
            * A:    set the  test variable for boundedness to false
            */
            isBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            saveStr = savedInstanceState.getString(STATE_USER);
        } else {
            saveStr = null;
        }

        try {
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            if (saveStr == null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.72220666573517, -97.28783313184977), 18.0f));
            }
            markerPoints = new ArrayList<>();
            latlngStrings = (EditText) findViewById(R.id.latlngStrings);
            Button sendLatLng = (Button) findViewById(R.id.sendLatLng);
            Button startRoute = (Button) findViewById(R.id.startRoute);
            Button stopRoute = (Button) findViewById(R.id.stopRoute);
            Button uadLoc = (Button) findViewById(R.id.uadLocation);

            /*
            * Service Connection Step 09:
            * A:    create intent to bind
            * B:    bind the intent, connection, context
            * */
            Intent intent01 = new Intent(this, BluetoothConnectionService.class);
            bindService(intent01, bluetoothConnection, Context.BIND_AUTO_CREATE);
            //Service Connection END Step 09:

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm =
                    (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            map = fm.getMap();

            //If there is information saved from a previous state
            if (saveStr != null) {
                //convert each token of the saveStr to LatLng obj then add to markerPonts
                savedStringToMarkerPoints(saveStr);
            }

            // onClickListener for the map. Add a marker for each touch
            map.setOnMapClickListener(
                new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        // Adding new item to the ArrayList
                        markerPoints.add(point);
                        // Adding the new marker to the map
                        MarkerOptions options = new MarkerOptions();
                        options.position(point);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        map.addMarker(options);
                    }
                }
            );

            //onLongClicklistener for the map. Clear map points when map is long touched
            map.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    public void onMapLongClick(LatLng point) {
                        try {
                            // Removes all the points from Google Map
                            map.clear();
                            // Removes all the points in the markerPoints ArrayList
                            markerPoints.clear();
                            //send clearroute command to UAD so that it clears its route
                            bluetoothConnectionServiceGMA.sendDataOverBluetooth("~clearroute");
                        }
                        catch (Exception e) {
                            showMessage("map.setOnMapLongClickListener() E ERROR");
                        }
                    }
                }
            );

            //sendLatLng Button Click
            sendLatLng.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        String str = "Sent:\n";
                        //Print to textbox all the latitude longitude strings
                        str += markerPoints.toString();
                        try {
                            str = markerPointsFormatString();
                        }
                        catch (Exception e) {
                            showMessage("markerPointsFormatString() E ERROR");
                        }
                        latlngStrings.setText(str);
                        transmitStringOnBluetooth();
                    }
                }
            );

            //startRoute Button Click
            startRoute.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            try {
                                //send the startroute command to the UADAP
                                bluetoothConnectionServiceGMA.sendDataOverBluetooth("~startroute");
                            }
                            catch (Exception e) {
                                showMessage("startRoute.setOnClickListener() E ERROR");
                            }
                        }
                    }
            );

            //stopRoute Button Click
            stopRoute.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            //send the stoproute command to the UADAP
                            bluetoothConnectionServiceGMA.sendDataOverBluetooth("~stoproute");
                        }
                        catch (Exception e) {
                            showMessage("stopRoute.setOnClickListener() E ERROR");
                        }
                    }
                }
            );

            //uadLoc Button Click
            uadLoc.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            //send the uadlocation command to the UADAP
                            bluetoothConnectionServiceGMA.sendDataOverBluetooth("~uadlocation");
                            //Ask for, receive, and then add the UAD's current location to the map
                            newUADMapPoint();
                        }
                        catch (Exception e) {
                            showMessage("uadLoc.setOnClickListener() E ERROR");
                        }
                    }
                }
            );
        }//END try under onCreate()
        catch (Exception e){
            showMessage("Error EXCEPTION e");
        }
    }//END onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        map.setMyLocationEnabled(true);
    }

    public void changeType(View view) {
        if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    //Method which zooms the user's view when the zoom button is clicked
    public void onZoom(View view) {
        if (view.getId() == R.id.Bzoomin) {
            map.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() == R.id.Bzoomout) {
            map.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

    //Method which searches google maps for addresses and commmon search results
    public void onSearch(View view) {
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;//list of addresses

        //Storage class for lat/long
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Address address = addressList.get(0);
            //get lat/lng of the address variable store it in a LatLng object
            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
            //add a marker to the above LatLng object
            map.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            map.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }
        catch (NullPointerException npe) {
            showMessage("NullPointer reference in onSerach() MapsActivity");
        }
    }

    //Method which creates the map fragment basics
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that there is no existing instantiated map
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    //Method which allows for user location services to be enabled
    private void setUpMap() {
        map.setMyLocationEnabled(true);
    }

    //Method which prints messages to the screen called "toasts" (the black box message screens)
    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_SHORT));
        msg.show();
    }

    //Method which requests, receives, then adds the UAD's location to the map.
    public void newUADMapPoint() {
        try {
            String newPointStr;
            /*
            * When the UAD sends a response to the uadlocation request sent by this application,
            * its response is stored in storeIncomingData. In order to add this information to the
            * map, the incoming string data is converted to a LatLng object, stored in the
            * ArrayList<LatLng> markerPoints, and then added as a marker to the map representing
            * that markerPoint.
            */
            newPointStr = bluetoothConnectionServiceGMA.storeIncomingData;;
            if (newPointStr != null) {
                showMessage(newPointStr);
                LatLng point = stringToLatLng(newPointStr);

                //clear the map of all points in order to add a new point
                map.clear();
                //Re add user location
                map.setMyLocationEnabled(true);
                //Re add all the old markers for markerPoints to the map
                for (int i = 0; i < markerPoints.size(); i++) {
                    MarkerOptions options1 = new MarkerOptions();
                    options1.position(markerPoints.get(i));
                    options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    map.addMarker(options1);
                }

                //Add the new UAD loc point requested by the click on Loc
                markerPoints.add(point);
                MarkerOptions options2 = new MarkerOptions();
                options2.position(point);
                options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                map.addMarker(options2);
                //Zoom the map view onto the new point
                map.animateCamera(CameraUpdateFactory.newLatLng(point));
            }
            else {
                showMessage("UAD data not available yet:\nPLEASE WAIT THEN TRY AGAIN");
            }
        }
        catch (Exception e) {
            showMessage("newMapPoint() ERROR");
        }
    }

    /*
    * Method which takes the saved instance string (onSaveInstance(saveStr) created when the
    * screen is rotated and the map object is destroyed) and parses and recreates the markerPoints
    * that were already on the screen prior to screen rotation
    */
    public void savedStringToMarkerPoints(String str) {
        double lat;
        double lng;
        String strLat;
        String strLng;
        String junk;
        LatLng tempLatLng;

        if (!str.equals("[]")) {
            StringTokenizer st = new StringTokenizer(str);
            while (st.hasMoreTokens()) {
                try {
                    strLat = st.nextToken(",");
                    strLng = st.nextToken();
                    StringTokenizer latTokenizer = new StringTokenizer(strLat);
                    junk = latTokenizer.nextToken("(");
                    strLat = latTokenizer.nextToken();
                    StringTokenizer lngTokenizer = new StringTokenizer(strLng);
                    strLng = lngTokenizer.nextToken(")");
                    lat = Double.parseDouble(strLat);
                    lng = Double.parseDouble(strLng);
                    tempLatLng = new LatLng(lat, lng);
                    markerPoints.add(tempLatLng);
                    MarkerOptions options1 = new MarkerOptions();
                    options1.position(tempLatLng);
                    options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    map.addMarker(options1);
                } catch (Exception e) {
                    showMessage("ERROR savedStringToLatLng(String str)");
                }

            }
        }
    }

    /*
    * Method which converts a string to a LatLng object for the purposes of adding a GPS point
    * requested then received from the UAD to the Google map
    *
    * STRING FORMAT INPUT MUST BE:
    * "Latitude,Longitude"
    *
    * It takes the string and tokenizes it using the "," as a delimeter
    * It then takes the first string token and converts it into a Latitude double variable
    * It then takes the second string token and converts it into a Longitude double variable
    * It then returns a new object of type LatLng, using the new Latitude and Longitude double
    * variables as parameters, to the calling Class
    * */
    public LatLng stringToLatLng(String str) {
        double lat;
        double lng;
        String strLat;
        String strLng;

        StringTokenizer st = new StringTokenizer(str);
        strLat = st.nextToken(",");
        strLng = st.nextToken();

        lat = Double.parseDouble(strLat);
        lng = Double.parseDouble(strLng);

        return new LatLng(lat, lng);
    }

    /*
    * Method which sends a properly formatted string of all the "latitude,longitude" strings
    * which are currently present in the ArrayList containing all the markerPoints currently
    * on the map in the order in which they were added by the user
    *
    * FORMAT of each entry is:
    *   "latitude,longitude\n"
    */
    public void transmitStringOnBluetooth() {
        String str;
        LatLng latLng;
        String tempLatString;
        String tempLngString;

        for (int i = 0; i < markerPoints.size(); i++) {

            str = "";
            tempLatString = "";
            tempLngString = "";

            try {


                latLng = markerPoints.get(i);
                ///tempLatString += latLng.latitude;
                //tempLngString += latLng.longitude;
                //String.format("%.6f", tempLatString);
                //String.format("%.6f", tempLatString);

                str = String.format("%d,%.6f,%.6f\n", i, latLng.latitude, latLng.longitude);

                /*
                str += i + 1;
                str += ",";
                //str += latLng.latitude;
                str += tempLatString;
                str += ",";
                //str += latLng.longitude + "\n";
                str += tempLngString;
                */
                //showMessage(str);
            }
            catch (Exception e) {
                showMessage("transmitStringOnBluetooth() ERROR");
            }
            try {
                bluetoothConnectionServiceGMA.mmOutputStream.write(str.getBytes());
            }
            catch (Exception e) {
                showMessage("sendLatLng.setOnClickListener() ERROR");
            }
        }
    }

    /*
    * Method which formats the LatLng objects in the markerPoints<LatLng> ArrayList into the
    * following printable format
    * 1:    latitude
    *       longitude
    */
    public String markerPointsFormatString() throws Exception {
        String str = "";
        LatLng latLng;

        for (int i = 0; i < markerPoints.size(); i++) {
            latLng = markerPoints.get(i);
            str += i + 1;
            str += ":\t";
            str += String.format("%.6f", latLng.latitude);
            str += "\n\t\t";
            str += String.format("%.6f\n", latLng.longitude);
        }

        return str;
    }

    /*
    * Method which stores the ArrayList<LatLng> markerPoints data in string form in order to pass
    * it back to a new instance of the MapsActivity. This is typically used when the screen is
    * rotated which destroys the current MapsActivity then recreates a new one in the changed
    * format. This method prevents the loss of markers added to the map by the user in the case of
    * screen rotation.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveStr = markerPoints.toString();
        outState.putString(STATE_USER, saveStr);
        super.onSaveInstanceState(outState);
    }

}//END MapsActivity.java