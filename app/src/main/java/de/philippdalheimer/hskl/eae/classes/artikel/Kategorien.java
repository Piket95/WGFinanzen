package de.philippdalheimer.hskl.eae.classes.artikel;

import android.util.Log;

import java.util.ArrayList;

public class Kategorien {

    //Objekt für den JSON-Response, wenn die Kategorien abgefragt werden

    public static boolean success;
    public static ArrayList<KategorieItem> categories;

    public static void printList(){
        for (KategorieItem i : categories){
            Log.d("TestApp", "Name: " + i.name);
            Log.d("TestApp", "Erstellt am: " + i.created);
        }
    }
}
