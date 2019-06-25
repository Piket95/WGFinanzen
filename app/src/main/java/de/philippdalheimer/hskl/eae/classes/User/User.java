package de.philippdalheimer.hskl.eae.classes.User;


import android.util.Log;

public class User {
    public static boolean success;
    public static MemberInfo member_info;


    public static void logUser(){
        Log.d("TestApp", Boolean.toString(success));

        if (member_info != null){
            member_info.printAll();
        }
        else{
            Log.d("TestApp", "MemberInfo ist NULL!");
        }

    }

}