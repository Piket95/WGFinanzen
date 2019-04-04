package de.philippdalheimer.hskl.eae.classes;


import android.util.Log;

public class User {
    public boolean success = false;
    public MemberInfo member_info = null;
    public Boolean is_admin = false;
    public double member_hours = 0.0;
    public String message = "";


    public void logUser(){
        Log.d("TestApp", Boolean.toString(success));
        Log.d("TestApp", Boolean.toString(is_admin));
        Log.d("TestApp", Double.toString(member_hours));
        Log.d("TestApp", message);

        if (member_info != null){
            member_info.printAll();
        }

    }

}