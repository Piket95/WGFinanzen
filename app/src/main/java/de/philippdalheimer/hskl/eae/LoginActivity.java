package de.philippdalheimer.hskl.eae;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnRegister;
    CardView loginCard;
    CardView registerCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Versteckt die Autogenerierte Actionbar im oberen Teil der App!
        getSupportActionBar().hide();

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Inputfelder evtl. anpassen
                EditText txtMail = findViewById(R.id.txt_login_mail);
                EditText txtPassword = findViewById(R.id.txt_login_password);

                if(!TextUtils.isEmpty(txtMail.getText().toString()) || !TextUtils.isEmpty(txtPassword.getText().toString())){
                    PostRequestLogin postRequestLogin = new PostRequestLogin();
                    //TODO: API_Key ändern
                    postRequestLogin.execute(txtMail.getText().toString(), txtPassword.getText().toString(), "8ubqB[uULJmIt8E3gkAa2x7MG7tAdMEdkNCl}kHyc]jP7hFY0JMqNH9Uz8zVjhTN");
                }
                else{
                    Toast.makeText(getApplicationContext(), "Bitte füllen Sie beide Felder aus!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtRegMail = findViewById(R.id.txt_register_mail);
                EditText txtRegPass = findViewById(R.id.txt_register_password_1);
                EditText txtRegConfPass = findViewById(R.id.txt_register_password_2);

                if(!TextUtils.isEmpty(txtRegMail.getText().toString()) || !TextUtils.isEmpty(txtRegPass.getText().toString()) || !TextUtils.isEmpty(txtRegConfPass.getText().toString())){
                    if(txtRegPass.getText().toString().equals(txtRegConfPass.getText().toString())){
                        PostRequestRegister postRequestRegister = new PostRequestRegister();
                        postRequestRegister.execute(txtRegMail.getText().toString(), txtRegPass.getText().toString());

                        Log.d("TestApp", "Yay stimmt!");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Die beiden Passwörter stimmen nicht überein!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    public void toggleCards (View v){
        loginCard = findViewById(R.id.card_login);
        registerCard = findViewById(R.id.card_register);

        if(loginCard.getVisibility() == View.VISIBLE){
            loginCard.setVisibility(View.GONE);
            registerCard.setVisibility(View.VISIBLE);
        }
        else{
            loginCard.setVisibility(View.VISIBLE);
            registerCard.setVisibility(View.GONE);
        }

    }

    private class PostRequestLogin extends AsyncTask<String, Void, User>{

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected User doInBackground(String... strings) {

            String mail = strings[0];
            String pass = strings[1];
            String api_key = strings[2];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("Anmeldung erfolgt...");
                    progressDialog.setTitle("Bitte warten Sie....");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }
            });

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("mail", mail)
                    .add("pass", pass)
                    .add("api_key", api_key)
                    .build();

            Request request = new Request.Builder()
                    .url("https://flbnet.philippdalheimer.de/api/login/login")
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){

                    String resultResponse = response.body().string();

                    Log.d("TestApp", "Request erfolgreich!");
                    Log.d("TestApp", resultResponse);

                    Gson gson = new Gson();

                    User user = gson.fromJson(resultResponse, User.class);
                    user.logUser();

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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(user != null){

                        if(user.success == true){

                            progressDialog.cancel();

                            Toast.makeText(getApplicationContext(), "Anmeldung erfolgreich!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Anmeldung fehlgeschlagen! Bitte überprüfen Sie Ihre Eingaben oder registrieren Sie sich!", Toast.LENGTH_LONG).show();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                }
                            });
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                            }
                        });
                        Toast.makeText(getApplicationContext(),"Irgendetwas scheint schief gelaufen zu sein!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private class PostRequestRegister extends AsyncTask<String, Void, User>{

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected User doInBackground(String... strings) {

            String mail = strings[0];
            String pass = strings[1];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setTitle("Bitte warten...");
                    progressDialog.setMessage("Sie werden nun registriert!");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }
            });

            //TODO: Post Request an API mit Mail und Passwort, sodass der User in Datenbank angelegt wird! Danach Weiterleitung auf Hauptseite mit registriertem Nutzer!


            return null;
        }
    }
}