package de.philippdalheimer.hskl.eae.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import de.philippdalheimer.hskl.eae.fragFeed;
import de.philippdalheimer.hskl.eae.fragWG_Beitreten;
import de.philippdalheimer.hskl.eae.fragZiele;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Fragment[] test = {new fragFeed(), new fragWG_Beitreten(), new fragZiele()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //TODO: If Abfrage ob eingeladen werden soll oder beigetreten werden soll!!!
        return test[position];
    }

    @Override
    public int getCount() {
        return test.length;
    }
}
