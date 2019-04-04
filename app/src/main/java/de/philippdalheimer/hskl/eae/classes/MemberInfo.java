package de.philippdalheimer.hskl.eae.classes;

import android.util.Log;

public class MemberInfo {

    public int id;
    public String mail;
    public String firstname;
    public String lastname;
    public String name;
    public String picture;
    public String created;


    public void printAll(){
        Log.d("TestApp", Integer.toString(id));
        Log.d("TestApp", mail);
        Log.d("TestApp", firstname);
        Log.d("TestApp", lastname);
        Log.d("TestApp", name);
        Log.d("TestApp", picture);
        Log.d("TestApp", created);
    }
}
