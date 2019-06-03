package de.philippdalheimer.hskl.eae.classes;

import android.util.Log;

public class regResultInfos {

    public boolean success = false;
    public String message = "Registrierung ist fehlgeschlagen!";

    public void print_all(){
        Log.d("TestApp", Boolean.toString(success));
        Log.d("TestApp", message);
    }
}
