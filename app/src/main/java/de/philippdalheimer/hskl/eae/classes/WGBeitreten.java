package de.philippdalheimer.hskl.eae.classes;

import android.util.Log;

public class WGBeitreten {

    public static boolean success;
    public static int wg_code;
    public static String message;

    public static void print_all(){
        Log.d("TestApp", "Success: " + Boolean.toString(success));
        Log.d("TestApp", "WGCode: " + Integer.toString(wg_code));
        Log.d("TestApp", "Message: " + message);
    }
}
