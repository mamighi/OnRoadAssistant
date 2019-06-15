package com.example.emad.onroadassistantapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    String username;
    String password;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_user_main);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button tvRegisterLink = (Button) findViewById(R.id.tvRegisterLink);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);



        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                //LoginActivity.this.startActivity(registerIntent);
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                new LoginAsync().execute(username, password);
            }
        });
    }


    private class LoginAsync extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpsURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String uname = params[0];
            String pass = params[1];

            try {

                // Enter URL address where your php file resides
                url = new URL("https://onroadservice.000webhostapp.com/Login.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpsURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread

            pdLoading.dismiss();
            JSONArray jsonArray = null;
            JSONObject jsonObject;
            String userID = "";
            String name = "";
            String username = "";
            String mobile = "";
            String email = "";
            String carmodel = "";
            String carnum = "";

            if (!result.equalsIgnoreCase("failure") || !result.equalsIgnoreCase("unsuccessful")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    userID = jsonArray.getJSONObject(0).getString("id");
                    name = jsonArray.getJSONObject(0).getString("name");
                    username = jsonArray.getJSONObject(0).getString("username");
                    mobile = jsonArray.getJSONObject(0).getString("mobile");
                    email = jsonArray.getJSONObject(0).getString("email");
                    carmodel = jsonArray.getJSONObject(0).getString("carmodel");
                    carnum = jsonArray.getJSONObject(0).getString("carnum");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(userID,name,username,mobile, email,carmodel,carnum);
            //   Toast.makeText(LoginActivity.this,"From loginpage userid:"+ userID + "::" + name, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                intent.putExtra("userid", userID);
                intent.putExtra("_name", name);
                intent.putExtra("uname", username);
                intent.putExtra("mobilenum", mobile);
                intent.putExtra("_email", email);
                intent.putExtra("_carmodel", carmodel);
                intent.putExtra("_carnum", carnum);

                startActivity(intent);
                LoginActivity.this.finish();

            } else {
                // If username and password does not match display a error message
                if (result.equalsIgnoreCase("failure"))
                    //Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    createdialog("Invalid email or password");
                else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                    //   Toast.makeText(LoginActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                    createdialog("OOPs! Something went wrong. Connection Problem");

                }
            }

        }
    }

    public void createdialog(final String msg) {
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.confirmlayout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LoginActivity.this);

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


}


