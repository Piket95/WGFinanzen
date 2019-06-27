package de.philippdalheimer.hskl.eae.classes.artikel;

import java.util.ArrayList;

public class ArtikelVonWG {

    //Objekt für den JSON-Response, wenn die artikel einer WG abgefragt werden

    public static boolean success;
    public static ArrayList<Artikel> artikel;

    public static void printList(){
        for (Artikel i : artikel){
            i.printArtikel();
        }
    }
}
