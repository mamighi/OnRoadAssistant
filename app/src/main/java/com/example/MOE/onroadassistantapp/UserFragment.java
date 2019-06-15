package com.example.emad.onroadassistantapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Emad on 6/10/2017.
 */

public class UserFragment extends Fragment {
    String userid ="";
    String name = "";
    String username = "";
    String mobile = "";
    String email = "";
    String carmodel = "";
    String carnum = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();
        userid = bundle.getString("");
        name = bundle.getString("_name");
        username = bundle.getString("_uname");
        mobile = bundle.getString("_mobile");
        email = bundle.getString("_email");
        carmodel =bundle.getString("_cmodel");
        carnum = bundle.getString("_carnum");
        View v = inflater.inflate(R.layout.fragment_new,container,false);
      //  TextView tid = (TextView) v.findViewById(R.id.tid);
        TextView tname = (TextView) v.findViewById(R.id.tName);
        TextView tuname = (TextView) v.findViewById(R.id.tUsername);
        TextView tmobile = (TextView) v.findViewById(R.id.tmobile);
        TextView temail = (TextView) v.findViewById(R.id.tmail);
        TextView _carmodel = (TextView) v.findViewById(R.id.tcarmodel);
        TextView _carnum = (TextView) v.findViewById(R.id.tcarNo);

     //  Toast.makeText(getActivity(),name, Toast.LENGTH_LONG).show();
     //   tid.setText   ("Userid:    "+name);
        tname.setText   ("Name:    "+name);
        tuname.setText  ("UserName: "+username);
        tmobile.setText ("Mobile No:"+mobile);
        temail.setText  ("Email:    "+email);
        _carmodel.setText("Car Model:"+carmodel);
        _carnum.setText  ("car No:   "+carnum);

        return v;
    }
}
