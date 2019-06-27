package de.philippdalheimer.hskl.eae;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.artikel.Artikel;
import de.philippdalheimer.hskl.eae.classes.artikel.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.user.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragFeed extends Fragment{

    ListView listView;
    View ctx;
    ArtikelVonWG artikelListeWG;
    FloatingActionButton btnArtikelHinzufügen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ctx = inflater.inflate(R.layout.fragment_feed, container, false);

        //Verknüpfen der Variable mit der ListView des Layouts "fragment_feed.xml"
        listView = ctx.findViewById(R.id.lv_feed);

        //Bei klick auf eines der Listeneinträge wird die ID des angeklickten Artikels ausgelesen,
        //der Activity "DetailsActivity" mitgegeben und diese Activity daraufhin aufgerufen
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                String selectedItemID = "-1";

                for (Artikel i : ArtikelVonWG.artikel){
                    if (i == parent.getItemAtPosition(position)){
                        selectedItemID = i.id;
                    }
                }

                Intent itemDetails = new Intent(getContext(), DetailsActivity.class);
                itemDetails.putExtra("ArtikelID", selectedItemID);
                startActivity(itemDetails);
            }
        });

        //Bei längerem Draufklicken erscheint ein Kontextmenü (AlertDialog) welches die Optionen Bearbeiten und Löschen beinhaltet,
        //um den angeklickten Eintrag wie die Namen schon sagen zu bearbeiten oder zu löschen
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //selectedItemID muss 2x angelegt werden, da eine Variable davon final sein muss, da sie innerhalb einer
                //Verschachtelung aufgerufen wird und eine darf nicht final sein, da sonst die Fehlermeldung "Variable 'selectedItemID' might be assigned in a loop" auftritt
                String selectedItemID1 = "-1";
                final String selectedItemID2;

                //Auslesen der ArtikelID des angeklickten Artikels, indem der angeklickte artikel mit allen Artikeln der Liste ArtikelVonWG.artikel abgeglichen wird
                for(Artikel i : ArtikelVonWG.artikel){
                    if(i == parent.getItemAtPosition(position)){
//                        Toast.makeText(getActivity(), i.name, Toast.LENGTH_SHORT).show();
                        selectedItemID1 = i.id;
                    }
                }

                selectedItemID2 = selectedItemID1;

                final String[] popupItems = {getResources().getString(R.string.feed_bearbeiten), getResources().getString(R.string.feed_loeschen)};

                //Bauen des AlertDialogs mit den 2 Einträgen Bearbeiten und Löschen
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                myBuilder
                        .setItems(popupItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getActivity(), popupItems[which], Toast.LENGTH_LONG).show();

                                switch (which){
                                    case 0: //Bearbeiten
                                        //Die Activity Neuer_Artikel bekommt die artikel ID mitgeliefert, damit dort die bisherigen Informationen des Artikels direkt in die Views
                                        //der Activity Neuer_Artikel eingetragen werden können. Anschließend wird diese aufgerufen und auf das Resultat gewartet -> "onActivityResult"
                                        Intent activityBearbeiten = new Intent(getContext(), Neuer_Artikel.class);
                                        activityBearbeiten.putExtra("ArtikelID", selectedItemID2);
                                        startActivityForResult(activityBearbeiten, 1001);
                                        break;
                                    case 1: //Löschen
                                        //POST-Request an Server, um den ausgewählten artikel zu löschen
//                                        Toast.makeText(getActivity(), "Löschen von " + selectedItemID2, Toast.LENGTH_SHORT).show();
                                        new deleteItem().execute(User.member_info.wg_code, selectedItemID2);
                                        break;
                                }
                            }
                        })
                        .show();

                return true;
            }
        });

        //"Button Hinzufügen" aus Layout an Variable binden
        btnArtikelHinzufügen = ctx.findViewById(R.id.btn_new_article_add);
        //Bei Klick auf den Button, wird die Activity "Neuer_Artikel" gestartet und auf das Resultat der Activity gewartet, welches in "onActivityResult" verarbeitet wird
        btnArtikelHinzufügen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent artikelNeu = new Intent(getContext(), Neuer_Artikel.class);
                startActivityForResult(artikelNeu, 1000);
            }
        });

        //Wenn der user ein wgcode besitzt, sprich in einer WG ist, wird die aktuelle Liste der WG über ein POST-Request abgefragt
        if(!User.member_info.wg_code.equals("-1")){
            GetWGListe getWGListe = new GetWGListe();
            getWGListe.execute(User.member_info.wg_code);
        }

        return ctx;
    }

    //Hier wird das Resultat der Activity "Neuer_Artikel" verarbeitet
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Nach anlegen des neuen Artikels, oder auch bearbeiten eines Artikels, wird die Liste neu über einen
        //POST-Request abgerufen, um die aktuelle Version der Liste anzuzeigen
        if((requestCode == 1000 || requestCode == 1001 ) && resultCode == Activity.RESULT_OK){
            new GetWGListe().execute(User.member_info.wg_code);
        }
    }

    //POST-Request zum Abruf der WG Liste (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    public class GetWGListe extends AsyncTask<String, Void, ArtikelVonWG> {

        @Override
        protected ArtikelVonWG doInBackground(String... strings) {
            String wg_code = strings[0];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_wgcode), wg_code)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_art_get))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if(response.isSuccessful()){
                    String resultResponse = response.body().string();

//                Log.d("TestApp", "Request erfolgreich!");
//                Log.d("TestApp", resultResponse);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

                    ArtikelVonWG artikelVonWG = gson.fromJson(resultResponse, ArtikelVonWG.class);

                    artikelListeWG = artikelVonWG;

                    return artikelVonWG;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } {

            }

            return null;
        }

        @Override
        protected void onPostExecute(ArtikelVonWG artikelVonWG) {
            super.onPostExecute(artikelVonWG);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    ArtikelVonWG.printList();

                    //Nachdem die Liste vom Server geladen wurde, wird ein neuer ListViewAdapter mit der Liste als Inhalt erstellt und mit der ListView des Layouts verknüpft
                    ListViewAdapter listViewAdapter = new ListViewAdapter(getContext(), ArtikelVonWG.artikel);
                    listView.setAdapter(listViewAdapter);
                }
            });
        }
    }

    //POST-Request zum löschen eines Eintrages aus der WG Liste (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    public class deleteItem extends AsyncTask<String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {
            String wgcode = strings[0];
            String artikel_id = strings[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_wgcode), wgcode)
                    .add(getResources().getString(R.string.req_artikel_id), artikel_id)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_art_remove))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if (response.isSuccessful()){
                    String resultResponse = response.body().string();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

                    MessageResponse messageResponse = gson.fromJson(resultResponse, MessageResponse.class);

                    return messageResponse;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);

            //Nach senden des POST-Requests, wird ein Toast mit der zurückgelieferten Nachricht angezeigt und die Liste über einen anderen POST-Request neu geladen
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), messageResponse.message, Toast.LENGTH_LONG).show();
                    new GetWGListe().execute(User.member_info.wg_code);
                }
            });
        }
    }
}
