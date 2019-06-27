package de.philippdalheimer.hskl.eae.classes.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.MainActivity;
import de.philippdalheimer.hskl.eae.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLogin extends AsyncTask <String, Void, User> {

    //Ablauf eines Logins eines Users

    Activity runningActivity;
    ProgressDialog progressDialog;

    public UserLogin(Activity activity){
        //Übergabe der Activity mit der diese Klasse aufgerufen wurde
        runningActivity = activity;
        progressDialog = new ProgressDialog(runningActivity);
    }

    //Hintergrund Task, der den user über einen POST-Request anmeldet und die Userinformationen erhält
    @Override
    protected User doInBackground(String... strings) {

        String username = strings[0];
        String pass = strings[1];

        //Meldung für Nutzer, dass er gerade angemeldet wird
        runningActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(runningActivity.getResources().getString(R.string.login_anmeldung_erfolgt));
                progressDialog.setTitle(runningActivity.getResources().getString(R.string.login_bitte_warten));
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        });

        //Aufbau des POST-Requests
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add(runningActivity.getResources().getString(R.string.req_username), username)
                .add(runningActivity.getResources().getString(R.string.req_password), pass)
                .build();

        Request request = new Request.Builder()
                .url(runningActivity.getResources().getString(R.string.url_login))
                .post(formBody)
                .build();

        //Abschicken des Requests und speichern des Responses
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()){

                String resultResponse = response.body().string();

//                    Log.d("TestApp", "Request erfolgreich!");
//                    Log.d("TestApp", resultResponse);

                //Umwandlung des JSON-Responses in die Klasse user (Daten in Klasse speichern)
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers()
                        .create();

                User user = gson.fromJson(resultResponse, User.class);
                user.member_info.password_clear = pass;
//                    user.logUser();

                return user;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final User user) {
        super.onPostExecute(user);

        //Beenden der Meldung, dass Nutzer angemeldet wird, Prüfung ob es zu Fehlern kam oder erfolgreich eingeloggt wurde und Weiterleitung auf Main Activity
        runningActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(user != null){

                    if(user.success == true){

                        progressDialog.cancel();

                        runningActivity.startActivity(new Intent(runningActivity, MainActivity.class));
//                        Toast.makeText(runningActivity.getApplicationContext(), runningActivity.getResources().getString(R.string.login_erfolgreich), Toast.LENGTH_LONG).show();
                        runningActivity.finish();
                    }
                    else{
                        Toast.makeText(runningActivity.getApplicationContext(), runningActivity.getResources().getString(R.string.login_fehlgeschlagen), Toast.LENGTH_LONG).show();

                        runningActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                            }
                        });
                    }
                }
                else{
                    runningActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    });
                    Toast.makeText(runningActivity.getApplicationContext(),runningActivity.getResources().getString(R.string.login_irgendwas_schiefgelaufen), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
