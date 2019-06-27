package de.philippdalheimer.hskl.eae;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.artikel.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.user.User;
import de.philippdalheimer.hskl.eae.classes.user.UserLogin;
import de.philippdalheimer.hskl.eae.classes.ViewPagerAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private Menu bottomNavMenu;
    private Menu mainMenu;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Neuer ViewPagerAdapter wird angelegt und mit ViewPager vom Layout verknüpft (zum Swipen zwischen den 3 Fragmenten)
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        //BottomNavigationView wird mit Variable verknüpft und mit einem ItemSelectedListener versehen, welcher ausgelöst wird, wenn auf eines der Items
        //manuell geklickt wird (onNavigationItemSelected)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        //Gehört der User keiner WG an, so bekommt der 2. Eintrag in der BottomNavigationView den Titel "WG beitreten" und das Icon ändert sich
        if (User.member_info.wg_code.equals("-1")) {
            bottomNavMenu = navigationView.getMenu();
            MenuItem groupPersonAdd = bottomNavMenu.findItem(R.id.nav_einladen_beitreten);
            groupPersonAdd.setIcon(R.drawable.ic_group_add_white_24dp);
            groupPersonAdd.setTitle(getResources().getString(R.string.menu_wg_beitreten));
        }

    }

    //Wenn das Menü oben rechts in der Ecke erstellt wird, wird geprüft, ob der User einer WG angehört
    //wenn Ja, wird der Eintrag "WG verlassen angezeigt". Gehört er keiner WG an, wir der Eintrag nicht angezeigt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Verknüpfen der Veriable mit dem MainMenu oben rechts
        mainMenu = menu;

        //Ist der Nutzer in keiner WG, wird der Menüpunkt WG verlassen ausgeblendet
        //Ausserdem startet der Nutzer direkt auf der 2. Seite der BottomNavView um direkt eine neue WG zu erstellen oder einer bestehenden beizutreten
        if (User.member_info.wg_code.equals("-1")) {
            menu.findItem(R.id.menu_wg_verlassen).setVisible(false);
            navigationView.setSelectedItemId(R.id.nav_einladen_beitreten);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //Hier wird überprüft, auf welchen Eintrag des Menüs oben rechts geklickt wurde und was dann passiert
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent login = new Intent(this, LoginActivity.class);

        switch (item.getItemId()) {
            case R.id.menu_wg_verlassen:
                //POST-Request an Server, dass aktuelle WG verlassen wird und anschließendes neues einloggen, um die Userinformationen zu aktualisieren
                new sendWGLeave().execute(User.member_info.username, User.member_info.wg_code);
                new UserLogin(this).execute(User.member_info.username, User.member_info.password_clear);
                break;
            case R.id.menu_logout:
                //Beim Logout, wird die Activity "LoginActivity" gestartet und die aktuelle Activity gestoppt
                startActivity(login);
                finish();
                break;
            case R.id.menu_refresh:
                //WG Artikelliste wird neu vom Server aus der Datenbank gezogen
                fragFeed feed = (fragFeed) viewPagerAdapter.getItem(0);

                feed.refresh();
                break;
        }

        //Prüfen ob das Feed ausgewählt ist, denn nur dann soll der Eintrag Aktualisieren im Menü oben rechts angezeigt werden
        if (navigationView.getSelectedItemId() != R.id.nav_feed) {
            mainMenu.findItem(R.id.menu_refresh).setVisible(false);
        } else {
            mainMenu.findItem(R.id.menu_refresh).setVisible(true);
        }

        return super.onOptionsItemSelected(item);
    }

    //Je nachdem welche Seite durch einen Swipe des ViewPagers aktuell sichtbar ist, wird auch dementsprechend das dazugehörige Item der BottomNavigationView ausgewählt
    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                navigationView.setSelectedItemId(R.id.nav_feed);
                break;
            case 1:
                navigationView.setSelectedItemId(R.id.nav_einladen_beitreten);
                break;
            case 2:
                navigationView.setSelectedItemId(R.id.nav_ziele);
                break;
        }

        //Prüfen ob das Feed ausgewählt ist, denn nur dann soll der Eintrag Aktualisieren im Menü oben rechts angezeigt werden
        if (navigationView.getSelectedItemId() != R.id.nav_feed) {
            //WG verlassen
            mainMenu.findItem(R.id.menu_refresh).setVisible(false);
        } else {
            mainMenu.findItem(R.id.menu_refresh).setVisible(true);
        }
    }

    //Je nachdem welche Seite durch einen Klick der BottomNavigationView ausgewählt wurde, wird auch dementsprechend die dazugehörige Seite im ViewPager angezeigt
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_feed:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.nav_einladen_beitreten:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.nav_ziele:
                viewPager.setCurrentItem(2);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        //Nothing to put in here!
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        //Nothing to put in here!
    }

    //POST-Request zum senden des Befehls "WG verlassen" (Beschreibung des Ablaufs siehe "./classes/user/UserLogin.java") (onPostExecute anders)
    private class sendWGLeave extends AsyncTask<String, Void, MessageResponse> {

        @Override
        protected MessageResponse doInBackground(String... strings) {

            String username = strings[0];
            String wgcode = strings[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add(getResources().getString(R.string.req_username), username)
                    .add(getResources().getString(R.string.req_wgcode), wgcode)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_wg_verlassen))
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful()) {
                    String responseResult = response.body().string();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers()
                            .create();

                    MessageResponse messageResponse = gson.fromJson(responseResult, MessageResponse.class);

                    return messageResponse;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {

            //Ausgabe der Nachricht der Rückgabe des POST-Requests in einem Toast
            Toast.makeText(MainActivity.this, messageResponse.message, Toast.LENGTH_LONG).show();

            super.onPostExecute(messageResponse);
        }
    }
}