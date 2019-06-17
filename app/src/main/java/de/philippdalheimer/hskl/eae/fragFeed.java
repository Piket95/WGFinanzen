package de.philippdalheimer.hskl.eae;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import de.philippdalheimer.hskl.eae.classes.Artikel;
import de.philippdalheimer.hskl.eae.classes.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;
import de.philippdalheimer.hskl.eae.classes.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragFeed extends Fragment {

    ListView listView;
    View ctx;
    ArtikelVonWG artikelListeWG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ctx = inflater.inflate(R.layout.fragment_feed, container, false);

        if(!User.member_info.wg_code.equals("-1")){
            GetWGListe getWGListe = new GetWGListe();
            getWGListe.execute(User.member_info.wg_code);
        }

        return ctx;
    }

    private class GetWGListe extends AsyncTask<String, Void, ArtikelVonWG> {

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

                String resultResponse = response.body().string();

                Log.d("TestApp", "Request erfolgreich!");
                Log.d("TestApp", resultResponse);

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers()
                        .create();

                ArtikelVonWG artikelVonWG = gson.fromJson(resultResponse, ArtikelVonWG.class);

                artikelListeWG = artikelVonWG;

                return artikelVonWG;

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
                    listView = ctx.findViewById(R.id.lv_feed);

                    ArtikelVonWG.printList();

                    ListViewAdapter listViewAdapter = new ListViewAdapter(getContext(), ArtikelVonWG.artikel);

                    listView.setAdapter(listViewAdapter);
                }
            });
        }
    }
}
