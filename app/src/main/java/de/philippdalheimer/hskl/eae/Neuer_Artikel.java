package de.philippdalheimer.hskl.eae;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import de.philippdalheimer.hskl.eae.classes.artikel.Artikel;
import de.philippdalheimer.hskl.eae.classes.artikel.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.artikel.KategorieItem;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.artikel.Kategorien;
import de.philippdalheimer.hskl.eae.classes.user.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Neuer_Artikel extends AppCompatActivity implements FloatingActionButton.OnClickListener {

    TextView lblTitle;
    EditText txtName;
    EditText txtBeschreibung;
    Spinner spKategorien;
    EditText txtDatum;
    EditText txtPreis;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    FloatingActionButton btnSend;

    String artikelID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer__artikel);

        Intent intent = getIntent();
        artikelID = intent.getStringExtra("ArtikelID");

        lblTitle = findViewById(R.id.lbl_new_article_title);
        txtName = findViewById(R.id.txt_new_article_name);
        txtBeschreibung = findViewById(R.id.txt_new_article_beschreibung);
        spKategorien = findViewById(R.id.txt_new_article_kategorie);
        txtDatum = findViewById(R.id.txt_new_article_datum);
        txtDatum.setOnClickListener(this);
        txtPreis = findViewById(R.id.txt_new_article_preis);

        //Wenn eine ArtikelID vorhanden ist, wird der dort gesetzte Artikel bearbeitet und das soll auch im Titel stehen
        if(artikelID != null){
            lblTitle.setText(getResources().getString(R.string.txt_new_article_titel_bearbeiten));
        }

        btnSend = findViewById(R.id.btn_new_article_submit);
        btnSend.setOnClickListener(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                ++m; // Monat + 1
                String date = d + "." + m + "." + y;
                txtDatum.setText(date);
            }
        };

        new getCategories().execute();
    }


    @Override
    public void onClick(View v) {

        //Wenn auf den DatePicker geklickt wurde, wird ein DatePickerDialog generiert und angezeigt
        if(v.getId() == R.id.txt_new_article_datum) {
            Calendar cal = Calendar.getInstance();
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH);
            int d = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    Neuer_Artikel.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    y, m, d
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        if(v.getId() == R.id.btn_new_article_submit){

            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> errors = new ArrayList<>();

            String error;

            //Wird auf den Button mit dem Häckchen geklickt, werden die Werte der Views in einer Liste gespeichert
            list.add(txtName.getText().toString());
            list.add(txtBeschreibung.getText().toString());
            list.add(Integer.toString(spKategorien.getSelectedItemPosition() + 1));
            list.add(txtDatum.getText().toString());
            list.add(txtPreis.getText().toString());

            // check, ob alle felder ausgefüllt sind
            if(list.get(0).equals(""))
            {
                errors.add("Name");
            }
            if(list.get(1).equals(""))
            {
                errors.add("Beschreibung");
            }
            if(list.get(3).equals(""))
            {
                errors.add("Datum");
            }
            if(list.get(4).equals(""))
            {
                errors.add("Preis");
            }

            // wenn errors vorhanden, errors anzeigen und weiteres Verfahren abbrechen
            if(!errors.isEmpty())
            {
                error = "Die Felder ";

                for (int i = 0; i < errors.size(); i++)
                {
                    if (i != errors.size()-1)
                    {
                        error += errors.get(i) + ", ";
                    }else{
                        error += errors.get(i) + " ";
                    }
                }

                error += "müssen ausgefüllt sein!";

                Toast.makeText(Neuer_Artikel.this, error, Toast.LENGTH_LONG).show();
                return;
            }

//            for(String i : list){
//                Log.d("TestApp", i);
//            }

            //Wurde eine ArtikelID von der Activity davor mitgegeben, wird der Inhalt der Liste über die "Artikel bearbeiten" API an den Server via POST geschickt
            //Wurde keine ID mitgegeben wird der Inhalt der Liste über die "Neuer Artikel anlegen" API an den Server via POST geschickt
            if(artikelID != null){
                new sendArtikelBearbeiten().execute(User.member_info.username, User.member_info.wg_code, artikelID, list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
            }
            else{
                new sendNeuerArtikel().execute(User.member_info.username,User.member_info.wg_code,list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
            }
        }
    }

    //POST-Request zum Anlegen eines neuen Artikels (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class sendNeuerArtikel extends AsyncTask<String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {
            String username = strings[0];
            String wgcode = strings[1];
            String art_name = strings[2];
            String art_beschreibung = strings[3];
            String art_cat_id = strings[4];
            String art_datum = strings[5];
            String art_preis = strings[6];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_wgcode), wgcode)
                    .add(getResources().getString(R.string.req_art_name), art_name)
                    .add(getResources().getString(R.string.req_art_beschreibung), art_beschreibung)
                    .add(getResources().getString(R.string.req_art_cat_id), art_cat_id)
                    .add(getResources().getString(R.string.req_art_datum), art_datum)
                    .add(getResources().getString(R.string.req_art_preis), art_preis)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_art_hinz))
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){

                String resultResponse = response.body().string();

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers()
                        .create();

                MessageResponse messageResponse = gson.fromJson(resultResponse, MessageResponse.class);

//                Log.d("TestApp", "[artikel anlegen Response] " + resultResponse);

                return messageResponse;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            //Ausgabe der zurückgegebenen Nachricht des POST-Requests über einen Toast an den Nutzer
            Toast.makeText(Neuer_Artikel.this, messageResponse.message, Toast.LENGTH_LONG).show();

            //Der Activity welche diese Activity aufgerufen hat, wird über "setResult" mitgeteilt, dass diese Activity mit ihrer Arbeit fertig ist und wird daraufhin beendet
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    //POST-Request zum Bearbeiten eines Artikels (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class sendArtikelBearbeiten extends AsyncTask<String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {
            String username = strings[0];
            String wgcode = strings[1];
            String artikel_id = strings[2];
            String artikel_name = strings[3];
            String artikel_beschreibung = strings[4];
            String artikel_cat_id = strings[5];
            String artikel_datum = strings[6];
            String artikel_preis = strings[7];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_wgcode), wgcode)
                    .add(getResources().getString(R.string.req_artikel_id), artikel_id)
                    .add(getResources().getString(R.string.req_artikel_name), artikel_name)
                    .add(getResources().getString(R.string.req_artikel_beschreibung), artikel_beschreibung)
                    .add(getResources().getString(R.string.req_artikel_cat_id), artikel_cat_id)
                    .add(getResources().getString(R.string.req_artikel_datum), artikel_datum)
                    .add(getResources().getString(R.string.req_artikel_preis), artikel_preis)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_art_edit))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if(response.isSuccessful()){

                    String requestResponse = response.body().string();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

                    MessageResponse messageResponse = gson.fromJson(requestResponse, MessageResponse.class);

                    return messageResponse;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            //Ausgabe der zurückgegebenen Nachricht des POST-Requests über einen Toast an den Nutzer
            Toast.makeText(Neuer_Artikel.this, MessageResponse.message, Toast.LENGTH_LONG).show();

            //Der Activity welche diese Activity aufgerufen hat, wird über "setResult" mitgeteilt, dass diese Activity mit ihrer Arbeit fertig ist und wird daraufhin beendet
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    //POST-Request zum Abruf der Kategorien (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class getCategories extends AsyncTask<String, Void, Kategorien>{

        @Override
        protected Kategorien doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_categorie))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                String resultResponse = response.body().string();

//                Log.d("TestApp", "Request erfolgreich!");
//                Log.d("TestApp", resultResponse);

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers()
                        .create();

                Kategorien kategorien = gson.fromJson(resultResponse, Kategorien.class);

                return kategorien;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Kategorien kategorien) {
            super.onPostExecute(kategorien);

            //Alle Kategorien die nun durch den POST-Request in der Klasse Kategorien in der Variable categories gespeichert wurden, werden nun alle durchgegangen und deren Namen in einer
            //neuen ArrayList gespeichert
            final ArrayList<String> katNamen = new ArrayList<>();

            for(KategorieItem i : kategorien.categories){
                katNamen.add(i.name);
            }

            //Der Spinner des Layouts dieser Activity bekommt nun die Namen aller Kategorien als Liste übermittelt und kann diese nun somit auch anzeigen
            ArrayAdapter<String> adapter = new ArrayAdapter(Neuer_Artikel.this, android.R.layout.simple_spinner_dropdown_item, katNamen);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spKategorien.setAdapter(adapter);

            //Wurde von der Activity, welche diese hier aufgerufen hat eine ArtikelID übergeben, werden alle Daten des Artikels dieser ID in die vorhandenen
            //Views des Layouts geschrieben (Artikel bearbeiten)
            if(artikelID != null){
                for (Artikel i : ArtikelVonWG.artikel){
                    if(i.id.equals(artikelID)){
                        txtName.setText(i.name);
                        txtBeschreibung.setText(i.beschreibung);
                        spKategorien.setSelection(Integer.parseInt(i.category_id) - 1);
                        txtDatum.setText(i.datum);
                        txtPreis.setText(i.preis);
                    }
                }
            }
        }
    }
}
