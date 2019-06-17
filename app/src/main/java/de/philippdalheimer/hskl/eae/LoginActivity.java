package de.philippdalheimer.hskl.eae;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.RegResult;
import de.philippdalheimer.hskl.eae.classes.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    //TODO: !ALLE! STRINGS MÜSSEN NOCH IN STRING RESOURCE DATEI AUSGELAGERT WERDEN!

    Button btnLogin;
    Button btnRegister;
    CardView loginCard;
    CardView registerCard;
    Intent main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        main = new Intent(this, MainActivity.class);

        //Versteckt die Autogenerierte Actionbar im oberen Teil der App!
        getSupportActionBar().hide();

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtUsername = findViewById(R.id.txt_login_username);
                EditText txtPassword = findViewById(R.id.txt_login_password);

                if(!TextUtils.isEmpty(txtUsername.getText().toString()) || !TextUtils.isEmpty(txtPassword.getText().toString())){
                    PostRequestLogin postRequestLogin = new PostRequestLogin();
                    postRequestLogin.execute(txtUsername.getText().toString(), txtPassword.getText().toString());
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
                EditText txtRegUsername = findViewById(R.id.txt_register_username);
                EditText txtRegPass = findViewById(R.id.txt_register_password_1);
                EditText txtRegConfPass = findViewById(R.id.txt_register_password_2);

                if(!TextUtils.isEmpty(txtRegUsername.getText().toString()) || !TextUtils.isEmpty(txtRegPass.getText().toString()) || !TextUtils.isEmpty(txtRegConfPass.getText().toString())){

                    if(txtRegPass.getText().toString().equals(txtRegConfPass.getText().toString())){
                        PostRequestRegister postRequestRegister = new PostRequestRegister();
                        postRequestRegister.execute(txtRegUsername.getText().toString(), txtRegPass.getText().toString());

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

            String username = strings[0];
            String pass = strings[1];

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
                    .add("username", username)
                    .add("password", pass)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/login/login")
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){

                    String resultResponse = response.body().string();

//                    Log.d("TestApp", "Request erfolgreich!");
//                    Log.d("TestApp", resultResponse);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

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

                            startActivity(main);
                            Toast.makeText(getApplicationContext(), "Anmeldung erfolgreich!", Toast.LENGTH_LONG).show();
                            finish();
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

    private class PostRequestRegister extends AsyncTask<String, Void, RegResult>{

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        String username;
        String pass;

        @Override
        protected RegResult doInBackground(String... strings) {

            this.username = strings[0];
            this.pass = strings[1];

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

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", pass)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/member/create")
                    .post(formBody)
                    .build();

            try(Response response = client.newCall(request).execute()){
                if(response.isSuccessful()){

                    String resultResponse = response.body().string();

                    Log.d("TestApp", "Request erfolgreich!");
                    Log.d("TestApp", resultResponse);

                    Gson gson = new Gson();

                    RegResult regResult = gson.fromJson(resultResponse, RegResult.class);

                    return regResult;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final RegResult regResult) {

            if (regResult != null){
                if(regResult.success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();

                            Toast.makeText(getApplicationContext(), regResult.message, Toast.LENGTH_LONG).show();
                            new PostRequestLogin().execute(username, pass);
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();

                            Toast.makeText(getApplicationContext(), regResult.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Irgendetwas scheint schief gelaufen zu sein! Bitte kontaktieren Sie einen Administrator!", Toast.LENGTH_SHORT).show();

            }

            super.onPostExecute(regResult);
        }
    }
}