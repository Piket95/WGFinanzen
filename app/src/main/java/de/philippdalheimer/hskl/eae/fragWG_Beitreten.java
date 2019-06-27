package de.philippdalheimer.hskl.eae;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.user.User;
import de.philippdalheimer.hskl.eae.classes.user.UserLogin;
import de.philippdalheimer.hskl.eae.classes.wg.WGBeitreten;
import de.philippdalheimer.hskl.eae.classes.wg.WGErstellen;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragWG_Beitreten extends Fragment {

    View ctx;
    Button btnEnterCode;
    Button btnWGErstellen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = inflater.inflate(R.layout.fragment_frag_wg__beitreten, container, false);

        //Verknüpfen des Buttons mit der View und Dialog anzeigen lassen bei Klick
        btnEnterCode = ctx.findViewById(R.id.btn_enter_wg_code);
        btnEnterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final EditText input = new EditText(getActivity());
                input.setHint(getResources().getString(R.string.diaEnter_hint));
                input.setTextSize(60);
                input.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setTitle(getResources().getString(R.string.diaEnter_title));
                builder.setPositiveButton(getResources().getString(R.string.dia_Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new enterWG().execute(User.member_info.username, input.getText().toString());
                    }
                });

                builder.show();
            }
        });

        //Verknüpfen des Buttons mit der View und POST-Request für WG erstellen ausführen
        btnWGErstellen = ctx.findViewById(R.id.btn_new_wg);
        btnWGErstellen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new newWG().execute(User.member_info.username);
            }
        });

        return ctx;
    }

    //POST-Request um der WG mit dem eingegebenen Code beizutreten (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class enterWG extends AsyncTask<String, Void, WGBeitreten> {

        @Override
        protected WGBeitreten doInBackground(String... strings) {
            String username = strings[0];
            String wgcode = strings[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_wgcode), wgcode)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_wg_beitreten))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if (response.isSuccessful()){
                    String responseResult = response.body().string();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

//                    Log.d("TestApp", "Response Enter WG: " + responseResult);

                    WGBeitreten wgBeitreten = gson.fromJson(responseResult, WGBeitreten.class);

//                    WGBeitreten.print_all();

                    return wgBeitreten;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(WGBeitreten wgBeitreten) {

            Toast.makeText(getContext(), WGBeitreten.message, Toast.LENGTH_LONG).show();

            //Erneute Anmeldung (als Refresh der Userinformationen), da sich die WG geändert hat bzw. der wgcode
            new UserLogin(getActivity()).execute(User.member_info.username, User.member_info.password_clear);

            super.onPostExecute(wgBeitreten);
        }
    }

    //POST Request um neue WG zu erstellen (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class newWG extends AsyncTask<String, Void, WGErstellen>{

        @Override
        protected WGErstellen doInBackground(String... strings) {
            String username = strings[0];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_wg_erstellen))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if (response.isSuccessful()){
                    String responseResult = response.body().string();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

//                    Log.d("TestApp", "Response Enter WG: " + responseResult);

                    WGErstellen wgErstellen = gson.fromJson(responseResult, WGErstellen.class);

//                    WGBeitreten.print_all();

                    return wgErstellen;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(WGErstellen wgErstellen) {

            Toast.makeText(getContext(), WGErstellen.message, Toast.LENGTH_LONG).show();

            //Erneute Anmeldung (als Refresh der Userinformationen), da sich die WG geändert hat bzw. der wgcode
            new UserLogin(getActivity()).execute(User.member_info.username, User.member_info.password_clear);

            super.onPostExecute(wgErstellen);
        }
    }
}
