package de.philippdalheimer.hskl.eae.classes.user;


import android.util.Log;

//Objekt für den JSON-Response, wenn die Userinformationen abgefragt werden

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