package de.philippdalheimer.hskl.eae.classes.Artikel;

import android.util.Log;

import java.util.ArrayList;

public class Kategorien {

    public static boolean success;
    public static ArrayList<KategorieItem> categories;

    public static void printList(){
        for (KategorieItem i : categories){
            Log.d("TestApp", "Name: " + i.name);
            Log.d("TestApp", "Erstellt am: " + i.created);
        }
    }
}
