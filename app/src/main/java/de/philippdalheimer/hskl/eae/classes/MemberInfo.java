package de.philippdalheimer.hskl.eae.classes;

import android.util.Log;

public class MemberInfo {

    public int id;
    public String username;
    public String password;
    public String created;


    public void printAll(){
        Log.d("TestApp", Integer.toString(id));
        Log.d("TestApp", password);
        Log.d("TestApp", username);
        Log.d("TestApp", created);
    }
}
