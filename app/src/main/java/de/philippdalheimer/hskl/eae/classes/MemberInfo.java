package de.philippdalheimer.hskl.eae.classes;

import android.util.Log;

public class MemberInfo {

    public static String id;
    public static String username;
    public static String password;
    public static String created;
    public static String wg_code;


    public static void printAll(){
        Log.d("TestApp", id);
        Log.d("TestApp", password);
        Log.d("TestApp", username);
        Log.d("TestApp", created);
        Log.d("TestApp", wg_code);
    }
}
