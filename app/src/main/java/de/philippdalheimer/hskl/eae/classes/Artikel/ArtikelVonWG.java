package de.philippdalheimer.hskl.eae.classes.Artikel;

import java.util.ArrayList;

public class ArtikelVonWG {

    public static boolean success;
    public static ArrayList<Artikel> artikel;

    public static void printList(){
        for (Artikel i : artikel){
            i.printArtikel();
        }
    }
}
