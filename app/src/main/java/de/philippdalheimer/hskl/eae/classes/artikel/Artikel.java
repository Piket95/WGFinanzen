package de.philippdalheimer.hskl.eae.classes.artikel;

import android.util.Log;

public class Artikel {

    //Attribute eines Artikels

    public String id;
    public String name;
    public String beschreibung;
    public String category_id;
    public String datum;
    public String preis;
    public String created;
    public String creator;
    public String category;

    public String[] getPreis(){
        String[] array = preis.split("\\.");

        return array;
    }

    public void printArtikel(){
        Log.d("TestApp", "ID: " + id);
        Log.d("TestApp", "Name: " + name);
        Log.d("TestApp", "Beschreibung: " + beschreibung);
        Log.d("TestApp", "Kategorie ID: " + category_id);
        Log.d("TestApp", "Datum: " + datum);
        Log.d("TestApp", "Preis: " + preis);
        Log.d("TestApp", "Euro: " + getPreis()[0]);
        Log.d("TestApp", "Cent: " + getPreis()[1]);
        Log.d("TestApp", "Erstellt am: " + created);
        Log.d("TestApp", "Erstellt von: " + creator);
        Log.d("TestApp", "Kategorie: " + category);
    }
}
