package de.philippdalheimer.hskl.eae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.philippdalheimer.hskl.eae.classes.Artikel;
import de.philippdalheimer.hskl.eae.classes.ArtikelVonWG;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String id = intent.getStringExtra("ArtikelID");

        for(Artikel i : ArtikelVonWG.artikel){
            if(i.id == id){
                //TODO: Alle Informationen aus dem Artikel in die UI schreiben!
            }
        }
    }
}
