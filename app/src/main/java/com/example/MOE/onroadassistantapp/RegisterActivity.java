package com.example.emad.onroadassistantapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etemail = (EditText) findViewById(R.id.etmail);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etmobile = (EditText) findViewById(R.id.etmobile);
        final EditText etcarmodel = (EditText) findViewById(R.id.etcarmodel);
        final EditText etcarnum = (EditText) findViewById(R.id.etcarNo);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String mobile =etmobile.getText().toString();
                final String email = etemail.getText().toString();
                final String carmodel = etcarmodel.getText().toString();
                final String carnum = etcarnum.getText().toString();

                String regexStr = "^[0-9]$";//mobile.matches(regexStr)==false
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if(!email.matches(emailPattern) &&(mobile.length()<10 || mobile.length()>13) ){
                   // Toast.makeText(getApplicationContext(),"Invalid email address & phone",Toast.LENGTH_SHORT).show();
                    createdialog("Invalid email and phone number");

                }

                else if(mobile.length()<10 || mobile.length()>13  ) {
                   // Toast.makeText(RegisterActivity.this,"Please enter "+"\n"+" valid phone number",Toast.LENGTH_SHORT).show();
                       createdialog("Invalid phone number"+mobile.length());
                    // am_checked=0;
                }
                else if (!email.matches(emailPattern))
                {
                   // Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                    createdialog("Invalid email");
                }
                else {


                    new SendPostRequest().execute(name, username, password, mobile, email, carmodel, carnum);
                     // createdialog("The user is successfully registered");
                }

            }
        });
    }
    public void createdialog(final String msg ){
        LayoutInflater li = LayoutInflater.from(RegisterActivity.this);
        View promptsView = li.inflate(R.layout.confirmlayout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                RegisterActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        TextView tvx = (TextView)promptsView.findViewById(R.id.textView12) ;
        tvx.setText(msg);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                if(msg.equals("The user is successfully created")) {
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
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


    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... params) {
            String _name = params[0];
            String _username = params[1];
            String pwd = params[2];
            String _mobile = params[3];
            String _email = params[4];
            String _cmodel = params[5];
            String _cnum = params[6];



            try {

                URL url = new URL("https://onroadservice.000webhostapp.com/Register.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("name", _name);
                postDataParams.put("username", _username);
                postDataParams.put("password", pwd);
                postDataParams.put("mobileNumber", _mobile);
                postDataParams.put("email", _email);
                postDataParams.put("carModel", _cmodel);
                postDataParams.put("carNumber", _cnum);
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
            //Toast.makeText(getApplicationContext(), result,
              //      Toast.LENGTH_LONG).show();
            if(result.equals("success")){
                createdialog("The user is successfully created");

            }else{
                createdialog("Error has occurred!");
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