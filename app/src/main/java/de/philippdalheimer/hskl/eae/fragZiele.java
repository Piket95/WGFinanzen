package de.philippdalheimer.hskl.eae;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;

import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.user.User;
import de.philippdalheimer.hskl.eae.classes.user.Ziele;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragZiele extends Fragment {

    View ctx;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    Switch aSwitch;
    LinearLayout cardVisible;
    ProgressBar progressBar;
    TextView lblMonth;
    TextView lblMaxZiel;
    TextView lblMaxZielEdit;
    SeekBar seekWunschZiel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = inflater.inflate(R.layout.fragment_frag_ziele, container, false);

        //Abrufen der Werte der Ziele des aktuellen Users (POST-Request)
        new getZiele().execute(User.member_info.username);

        cardVisible = ctx.findViewById(R.id.linLay_Ziele_Card);

        //Initialisierung der SharedPreferences
        pref = getActivity().getSharedPreferences(getActivity().getResources().getString(R.string.sh_filename) + User.member_info.id, Context.MODE_PRIVATE);
        edit = pref.edit();

        aSwitch = ctx.findViewById(R.id.swt_ziele_track);

        //Abfrage über SharedPreferences, ob der Switch vorher schon aktiviert wurde und dementsprechend aktivieren oder deaktiviert lassen
        if(pref.getString(getActivity().getResources().getString(R.string.sh_sw_user), "").equals(User.member_info.username)){
            aSwitch.setChecked(pref.getBoolean(getActivity().getResources().getString(R.string.sh_sw_value), false ));
        }

        //Wenn Switch aktiviert ist, wird auch die Karte mit den Einstellungen sichtbar
        if(aSwitch.isChecked()){
            cardVisible.setVisibility(View.VISIBLE);
        }

        //Wenn der Switch betätigt wird, wird die Karte mit den Einstellungen angezeigt oder versteckt sowie
        //der Wert ob der Switch an oder aus ist, in der SharedPreferences Datei gespeichert
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cardVisible.setVisibility(View.VISIBLE);

                    edit.putString(getActivity().getResources().getString(R.string.sh_sw_user), User.member_info.username);
                    edit.putBoolean(getActivity().getResources().getString(R.string.sh_sw_value), true);
                    edit.commit();
                }
                else{
                    cardVisible.setVisibility(View.GONE);

                    edit.putString(getActivity().getResources().getString(R.string.sh_sw_user), User.member_info.username);
                    edit.putBoolean(getActivity().getResources().getString(R.string.sh_sw_value), false);
                    edit.commit();
                }
            }
        });

        progressBar = ctx.findViewById(R.id.progressBar);
        lblMonth = ctx.findViewById(R.id.lbl_month);
        lblMaxZiel = ctx.findViewById(R.id.lbl_max_limit_1);
        lblMaxZielEdit = ctx.findViewById(R.id.lbl_max_limit_2);
        lblMaxZiel = ctx.findViewById(R.id.lbl_max_limit_1);

        //Das Label lblMonth soll den aktuellen Monat sowie das aktuelle Jahr beinhalten
        String[] monate = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
        Calendar calendar = Calendar.getInstance();
        lblMonth.setText(monate[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR));

        //Verknüpfen der Variable mit der Seekbar ganz unten im Layout
        seekWunschZiel = ctx.findViewById(R.id.seb_max_limit);
        seekWunschZiel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Wird an der Seekbar gezogen, verändern sich direkt die TextViews und geben den aktuellen Wert an
                progressBar.setMax(progress);

                lblMaxZiel.setText(progress + " €");
                lblMaxZielEdit.setText("Maximum bearbeiten (" + progress + " €)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Ist das einstellen der Seekbar abgeschlossen, werden die eingestellten Daten über einen POST-Request an den Server übergeben und dort gespeichert
                //sowie die Variable "goal" in der Klasse "Ziele" mit dem neuen Wert aktualisiert
                Ziele.goal = Integer.toString(seekBar.getProgress());
                new setZiele().execute(User.member_info.username, Integer.toString(seekBar.getProgress()));
            }
        });

        return ctx;
    }

    //POST-Request zum Abruf der Ziele eines Users (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class getZiele extends AsyncTask <String, Void, Ziele>{

        @Override
        protected Ziele doInBackground(String... strings) {
            String username = strings[0];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_ziele_get))
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){

                    String resultResponse = response.body().string();

//                    Log.d("TestApp", resultResponse);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

                    Ziele ziele = gson.fromJson(resultResponse, Ziele.class);

                    return ziele;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Ziele ziele) {
            //Die Views erhalten die Werte, welche durch den POST-Request vom Server zurückgegeben wurden
            progressBar.setMax(Integer.parseInt(Ziele.goal));
            progressBar.setProgress((int) Double.parseDouble(Ziele.current_value));

            lblMaxZiel.setText(Ziele.goal + " €");
            lblMaxZielEdit.setText("Maximum bearbeiten (" + Ziele.goal + " €)");
            seekWunschZiel.setProgress((int) Double.parseDouble(Ziele.goal));

            super.onPostExecute(ziele);
        }
    }

    //POST-Request zum setzen des neuen Ziels eines Users (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class setZiele extends AsyncTask <String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {
            String username = strings[0];
            String goal = strings [1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_goal), goal)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_ziele_set))
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){

                    String resultResponse = response.body().string();

//                    Log.d("TestApp", resultResponse);

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
        protected void onPostExecute(MessageResponse messageResponse) {

            //Toast wird angezeigt, welcher ausgibt, dass das Ziel erfolgreich (oder nicht erfolgreich) gesetzt wurde
            Toast.makeText(getActivity(), MessageResponse.message, Toast.LENGTH_LONG).show();

            super.onPostExecute(messageResponse);
        }
    }
}
