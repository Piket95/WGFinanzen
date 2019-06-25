package de.philippdalheimer.hskl.eae;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.User;
import de.philippdalheimer.hskl.eae.classes.UserLogin;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    //TODO: !ALLE! STRINGS MÜSSEN NOCH IN STRING RESOURCE DATEI AUSGELAGERT WERDEN!
    //TODO: !ALLE! Dateien müssen noch kommentiert werden!

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
                    //Aufruf der Klasse UserLogin, um die Anmeldeinformationen mit den Datenbank abzugleichen (POST Request)
                    new UserLogin(LoginActivity.this).execute(txtUsername.getText().toString(), txtPassword.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_felder_ausfüllen), Toast.LENGTH_LONG).show();
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

//                        Log.d("TestApp", "Yay stimmt!");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_passwort_identisch), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    //Wechsel zwischen Login Sicht und Registrierungssicht
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

    //POST Request zur Registrierung/Anlegen eines neuen Benutzers
    private class PostRequestRegister extends AsyncTask<String, Void, MessageResponse>{

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        String username;
        String pass;

        @Override
        protected MessageResponse doInBackground(String... strings) {

            this.username = strings[0];
            this.pass = strings[1];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setTitle(getResources().getString(R.string.login_bitte_warten));
                    progressDialog.setMessage(getResources().getString(R.string.reg_register_erfolgt));
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }
            });

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_password), pass)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_register))
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

            if (messageResponse != null){
                if(messageResponse.success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();

                            Toast.makeText(getApplicationContext(), messageResponse.message, Toast.LENGTH_LONG).show();
                            new UserLogin(LoginActivity.this).execute(username,pass);
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();

                            Toast.makeText(getApplicationContext(), messageResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_irgendwas_schiefgelaufen), Toast.LENGTH_SHORT).show();

            }

            super.onPostExecute(messageResponse);
        }
    }
}