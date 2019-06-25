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

import de.philippdalheimer.hskl.eae.classes.Artikel.Artikel;
import de.philippdalheimer.hskl.eae.classes.Artikel.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.User.User;
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

        listView = ctx.findViewById(R.id.lv_feed);

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItemID1 = "-1";
                final String selectedItemID2;

                for(Artikel i : ArtikelVonWG.artikel){
                    if(i == parent.getItemAtPosition(position)){
//                        Toast.makeText(getActivity(), i.name, Toast.LENGTH_SHORT).show();
                        selectedItemID1 = i.id;
                    }
                }

                selectedItemID2 = selectedItemID1;

                final String[] popupItems = {"Bearbeiten", "Löschen"};

                AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                myBuilder
                        .setItems(popupItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getActivity(), popupItems[which], Toast.LENGTH_LONG).show();

                                switch (which){
                                    case 0: //Bearbeiten
                                        Intent activityBearbeiten = new Intent(getContext(), Neuer_Artikel.class);
                                        activityBearbeiten.putExtra("ArtikelID", selectedItemID2);
                                        startActivityForResult(activityBearbeiten, 1001);
                                        break;
                                    case 1: //Löschen
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

        btnArtikelHinzufügen = ctx.findViewById(R.id.btn_new_article_add);
        btnArtikelHinzufügen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent artikelNeu = new Intent(getContext(), Neuer_Artikel.class);
                startActivityForResult(artikelNeu, 1000);
            }
        });

        if(!User.member_info.wg_code.equals("-1")){
            GetWGListe getWGListe = new GetWGListe();
            getWGListe.execute(User.member_info.wg_code);
        }

        return ctx;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == 1000 || requestCode == 1001 ) && resultCode == Activity.RESULT_OK){
            new GetWGListe().execute(User.member_info.wg_code);
        }
    }

    public class GetWGListe extends AsyncTask<String, Void, ArtikelVonWG> {

        @Override
        protected ArtikelVonWG doInBackground(String... strings) {
            String wg_code = strings[0];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("wgcode", wg_code)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/artikel/get")
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

                    ListViewAdapter listViewAdapter = new ListViewAdapter(getContext(), ArtikelVonWG.artikel);

                    listView.setAdapter(listViewAdapter);
                }
            });
        }
    }

    public class deleteItem extends AsyncTask<String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {
            String wgcode = strings[0];
            String artikel_id = strings[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("wgcode", wgcode)
                    .add("artikel_id", artikel_id)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/artikel/remove")
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
