package com.example.emad.onroadassistantapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.security.AccessController.getContext;


public class UserMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, newFragment.OnFragmentInteractionListener {


    private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    String userID="";
    String name="";
    String username="";
    String mobile="";
    String email="";
    String carmodel="";
    String carnum="";
    Context contxt;
    private EditText result;

    // Session Manager Class
    SessionManager session;

    ContextGetter contextGetter;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    // Button Logout
    Button btnLogout;

    //LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Session class instance

     /*   userID = getIntent().getExtras().getString("userid");
        name = getIntent().getExtras().getString("_name");
        username = getIntent().getExtras().getString("uname");
        mobile = getIntent().getExtras().getString("mobilenum");
        email = getIntent().getExtras().getString("_email");
        carmodel = getIntent().getExtras().getString("_carmodel");
        carnum = getIntent().getExtras().getString("_carnum");*/


       // btnLogout = (Button)
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        userID = user.get(SessionManager.KEY_USERID);
       // name
        name = user.get(SessionManager.KEY_NAME);
        username = user.get(SessionManager.KEY_USER);
        mobile = user.get(SessionManager.KEY_MOBILE);

        // email
        email = user.get(SessionManager.KEY_EMAIL);
        carmodel = user.get(SessionManager.KEY_CMODEL);
        carnum = user.get(SessionManager.KEY_CNUM);

     //   Toast.makeText(UserMainActivity.this, userID, Toast.LENGTH_LONG).show();


        com.cuboid.cuboidcirclebutton.CuboidButton tyrebtn = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.tyreButton);
        tyrebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _service = "tyre repair";
                String adress="";
                if(latitude == 0.0 || longitude == 0.0){
                    adress = "null";
                    createdialogError("Location is unavailable");

                }else{

                    try {
                        adress =  getAddress( latitude,longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createdialog("100",userID,_service,adress);

                }

            }
        });
        com.cuboid.cuboidcirclebutton.CuboidButton enginebtn = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.engineButton);
        enginebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _service = "engine breakdown";
                String adress="";

                if(latitude == 0.0 || longitude == 0.0){
                    adress = "null";
                    createdialogError("Location is unavailable");

                }else{

                    try {
                        adress =  getAddress( latitude,longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createdialog("200",userID,_service,adress);

                }

            }
        });
        com.cuboid.cuboidcirclebutton.CuboidButton towbtn = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.towButton);
        towbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _service = "tow car";
                String adress="";
                if(latitude == 0.0 || longitude == 0.0){
                    adress = "null";
                    createdialogError("Location is unavailable");

                }else{

                    try {
                        adress =  getAddress( latitude,longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createdialog("250",userID,_service,adress);

                }

            }
        });
        com.cuboid.cuboidcirclebutton.CuboidButton htowbtn = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.htowButton);
        htowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _service = "tow car on highway";
                String adress="";
                if(latitude == 0.0 || longitude == 0.0){
                    adress = "null";
                    createdialogError("Location is unavailable");

                }else{

                    try {
                        adress =  getAddress( latitude,longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createdialog("250",userID,_service,adress);

                }

            }
        });
        com.cuboid.cuboidcirclebutton.CuboidButton callbtn = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.callButton);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _service = "Battery issue";
                String adress="";
                if(latitude == 0.0 || longitude == 0.0){
                    adress = "null";
                    createdialogError("Location is unavailable");

                }else{

                    try {
                        adress =  getAddress( latitude,longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createdialog("150",userID,_service,adress);

                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //----------------------navigation header----------------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView m_name = (TextView) header.findViewById(R.id.name);
        m_name.setText(name);
        TextView m_mail = (TextView) header.findViewById(R.id.tv1);
        m_mail.setText(email);
       // navigationView.getMenu().getItem(2).setOnMenuItemClickListener(new)




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void createdialog(final String rate,final String uid, final String srvce, final String addr){
        LayoutInflater li = LayoutInflater.from(UserMainActivity.this);
        View promptsView = li.inflate(R.layout.message_input_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                UserMainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        final TextView price =(TextView) promptsView.findViewById(R.id.price);
        price.setText("Service Charges: RM "+rate);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                  String message = userInput.getText().toString();
                                  //ServiceData sdata = new ServiceData(uid,srvce,addr,message);
                                  new ServicesData().execute(uid,srvce,addr,message);


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void createdialogError(final String msg) {
        LayoutInflater li = LayoutInflater.from(UserMainActivity.this);
        View promptsView = li.inflate(R.layout.confirmlayout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                UserMainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        TextView tvx = (TextView) promptsView.findViewById(R.id.textView12);
        tvx.setText(msg);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                //  if(msg.equals("The user is successfully created")) {
                                //    Intent intent = new Intent(LoginActivity.this,UserMainActivity.class);
                                //   startActivity(intent);
                                // }
                            }
                        });
              /*  .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });*/

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
       //     return true;
       // }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            fragment = new UserFragment();

            if (fragment != null) {
                Bundle bundle = new Bundle();
                bundle.putString("_name", name);
                bundle.putString("_uname", username);
                bundle.putString("_mobile", mobile);
                bundle.putString("_email", email);
                bundle.putString("_mobile", mobile);
                bundle.putString("_cmodel", carmodel);
                bundle.putString("_carnum", carnum);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.flayout1, fragment,"usertag");
                ft.commit();
            }
            //navItemIndex = 1;

       } else if (id == R.id.nav_share) {
            session.logoutUser();

        }else if (id == R.id.nav_home) {

                    Fragment frag = getSupportFragmentManager().findFragmentByTag("usertag");
                    if(frag!=null) getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
// new addition for enabling location...............
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(UserMainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                      //  settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private String getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
            List<Address> addresses;
            String add="";
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if(!address.equals(null)){
                add= address;
            }
            String city = addresses.get(0).getLocality();
            if(!city.equals(null)){
                add= add+","+city;
            }
            String state = addresses.get(0).getAdminArea();
            if(!state.equals(null)){
                add= add+","+state;
            }
            String country = addresses.get(0).getCountryName();
                add= add+","+country;
            //String postalCode = addresses.get(0).getPostalCode();
            //String add=addresses.get(0).getAddressLine(0)+","
          //  String addp= addresses.get(0).getSubAdminArea()+"+"
            //        +addresses.get(0).getSubLocality();//+"+"+city+"+"+state;
            // state=addresses.get(0).getAdminArea();*/
       // if()
        return add;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(UserMainActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {


                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public class ServicesData extends AsyncTask<String, Void, String> {



        protected void onPreExecute(){}

        protected String doInBackground(String... params) {
            String userid = params[0];
            String service = params[1];
            String addr = params[2];
            String message = params[3];


            try {

                URL url = new URL("https://onroadservice.000webhostapp.com/serviceRequest.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("userid", userid);
                postDataParams.put("service", service);
                postDataParams.put("location", addr);
                postDataParams.put("message", message);
                Log.e("params",postDataParams.toString());

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationCo
            if(result.equalsIgnoreCase("success")){
               // Toast.makeText(UserMainActivity.this, result, Toast.LENGTH_LONG).show();
                createdialogError("Request sent successfully!");
            }else {
                createdialogError("An Error has occurred!Try again.");
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
