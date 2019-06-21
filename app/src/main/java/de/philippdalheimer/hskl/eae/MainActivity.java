package de.philippdalheimer.hskl.eae;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.philippdalheimer.hskl.eae.classes.ArtikelVonWG;
import de.philippdalheimer.hskl.eae.classes.User;
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
        }
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
}
