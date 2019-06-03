package de.philippdalheimer.hskl.eae.classes;


import android.util.Log;

public class User {
    public boolean success = false;
    public MemberInfo member_info = null;


    public void logUser(){
        Log.d("TestApp", Boolean.toString(success));

        if (member_info != null){
            member_info.printAll();
        }

    }

}