package de.philippdalheimer.hskl.eae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import de.philippdalheimer.hskl.eae.classes.Artikel;
import de.philippdalheimer.hskl.eae.classes.ArtikelVonWG;

public class DetailsActivity extends AppCompatActivity {

    ImageView imgCategorieIcon;
    TextView lblArtikelname;
    TextView lblBeschreibung;
    TextView lblKategorie;
    TextView lblDatum;
    TextView lblPreis;
    TextView lblErsteller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String strId = intent.getStringExtra("ArtikelID");

//        Log.d("TestApp", "ID: " + strId);

        imgCategorieIcon = findViewById(R.id.img_details_kategorie);
        lblArtikelname = findViewById(R.id.lbl_details_artikelname);
        lblBeschreibung = findViewById(R.id.lbl_details_beschreibung);
        lblKategorie = findViewById(R.id.lbl_details_kategorie);
        lblDatum = findViewById(R.id.lbl_details_erstellt_am);
        lblPreis = findViewById(R.id.lbl_details_preis);
        lblErsteller = findViewById(R.id.lbl_details_erstellt_von);

        for(Artikel i : ArtikelVonWG.artikel){
            if(i.id.equals(strId)){
//                Log.d("TestApp", i.name);

                switch (Integer.parseInt(i.category_id)){
                    case 1:
                        imgCategorieIcon.setImageResource(R.drawable.lebensmittel);
                        break;
                    case 2:
                        imgCategorieIcon.setImageResource(R.drawable.unterhaltung);
                        break;
                    case 3:
                        imgCategorieIcon.setImageResource(R.drawable.freizeit);
                        break;
                    case 4:
                        imgCategorieIcon.setImageResource(R.drawable.haushalt);
                        break;
                    default:
                        break;
                }

                lblArtikelname.setText(i.name);
                lblBeschreibung.setText(i.beschreibung);
                lblKategorie.setText(i.category);
                lblDatum.setText(i.datum);
                lblPreis.setText(i.preis);
                lblErsteller.setText(i.creator);
            }
        }
    }
}
