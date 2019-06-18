package de.philippdalheimer.hskl.eae;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import de.philippdalheimer.hskl.eae.classes.KategorieItem;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.Kategorien;
import de.philippdalheimer.hskl.eae.classes.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Neuer_Artikel extends AppCompatActivity implements FloatingActionButton.OnClickListener {

    EditText txtName;
    EditText txtBeschreibung;
    Spinner spKategorien;
    EditText txtDatum;
    EditText txtPreis;

    FloatingActionButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer__artikel);

        txtName = findViewById(R.id.txt_new_article_name);
        txtBeschreibung = findViewById(R.id.txt_new_article_beschreibung);
        spKategorien = findViewById(R.id.txt_new_article_kategorie);
        txtDatum = findViewById(R.id.txt_new_article_datum);
        txtPreis = findViewById(R.id.txt_new_article_preis);

        btnSend = findViewById(R.id.btn_new_article_submit);
        btnSend.setOnClickListener(this);

        new getCategories().execute();

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_new_article_submit){

            ArrayList<String> list = new ArrayList<>();

            list.add(txtName.getText().toString());
            list.add(txtBeschreibung.getText().toString());
            list.add(Integer.toString(spKategorien.getSelectedItemPosition() + 1));
            list.add(txtDatum.getText().toString());
            list.add(txtPreis.getText().toString());

//            for(String i : list){
//                Log.d("TestApp", i);
//            }

            //TODO: Hier brauchen wir die KategorieID nicht den Namen
            new sendNeuerArtikel().execute(User.member_info.username,User.member_info.wg_code,list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
        }
    }

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
                    .add("username", username)
                    .add("wgcode", wgcode)
                    .add("art_name", art_name)
                    .add("art_beschreibung", art_beschreibung)
                    .add("art_cat_id", art_cat_id)
                    .add("art_datum", art_datum)
                    .add("art_preis", art_preis)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/artikel/create")
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){

                String resultResponse = response.body().string();

//                Gson gson = new GsonBuilder()
//                        .excludeFieldsWithModifiers()
//                        .create();

//                MessageResponse messageResponse = gson.fromJson(resultResponse, MessageResponse.class);

//                Log.d("TestApp", resultResponse);

                return null;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            //Unschön ich weiß aber es kommt irgendwie ein json mit 13 vorher zurück welches nicht deserialisiert werden kann!
            //13{"success":true,"message":"Artikel wurde angelegt."}
            Toast.makeText(Neuer_Artikel.this, "Artikel wurde angelegt.", Toast.LENGTH_LONG).show();

            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    private class getCategories extends AsyncTask<String, Void, Kategorien>{

        @Override
        protected Kategorien doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/artikel/get_categories")
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

            final ArrayList<String> katNamen = new ArrayList<>();

            for(KategorieItem i : kategorien.categories){
                katNamen.add(i.name);
            }
//
            ArrayAdapter<String> adapter = new ArrayAdapter(Neuer_Artikel.this, android.R.layout.simple_spinner_dropdown_item, katNamen);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spKategorien.setAdapter(adapter);
        }
    }
}
