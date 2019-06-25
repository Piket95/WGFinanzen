package de.philippdalheimer.hskl.eae;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import java.util.zip.Inflater;

import de.philippdalheimer.hskl.eae.classes.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.MessageResponse;
import de.philippdalheimer.hskl.eae.classes.User;
import de.philippdalheimer.hskl.eae.classes.UserLogin;
import de.philippdalheimer.hskl.eae.classes.ViewPagerAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private Menu bottomNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(this);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        if(User.member_info.wg_code.equals("-1")){
            bottomNavMenu = navigationView.getMenu();
            MenuItem groupPersonAdd = bottomNavMenu.findItem(R.id.nav_einladen_beitreten);
            groupPersonAdd.setIcon(R.drawable.ic_group_add_white_24dp);
            groupPersonAdd.setTitle("WG beitreten");
            navigationView.setSelectedItemId(R.id.nav_einladen_beitreten);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if(User.member_info.wg_code.equals("-1")){
            menu.findItem(R.id.menu_wg_verlassen).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent login = new Intent(this, LoginActivity.class);

        switch (item.getItemId()){
            case R.id.menu_wg_verlassen:
                new sendWGLeave().execute(User.member_info.username, User.member_info.wg_code);
                new UserLogin(this).execute(User.member_info.username, User.member_info.password_clear);
                break;
            case R.id.menu_logout:
                startActivity(login);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new getWGList().execute("896142");
    }

    @Override
    public void onPageSelected(int i) {
        switch (i){
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
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

    private class sendWGLeave extends AsyncTask <String, Void, MessageResponse>{

        @Override
        protected MessageResponse doInBackground(String... strings) {

            String username = strings[0];
            String wgcode = strings[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("wgcode", wgcode)
                    .build();

            Request request = new Request.Builder()
                    .url("https://hskl.philippdalheimer.de/api/wg/leave")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()){

                if (response.isSuccessful()){
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

            Toast.makeText(MainActivity.this, messageResponse.message, Toast.LENGTH_LONG).show();

            super.onPostExecute(messageResponse);
        }
    }
}
