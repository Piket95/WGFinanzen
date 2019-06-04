package de.philippdalheimer.hskl.eae.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.philippdalheimer.hskl.eae.fragFeed;
import de.philippdalheimer.hskl.eae.fragWG_Beitreten;
import de.philippdalheimer.hskl.eae.fragZiele;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new fragFeed();
            case 1:
                //TODO: If Abfrage ob eingeladen werden soll oder beigetreten werden soll!!!
                return new fragWG_Beitreten();
//            return new fragBewohner_einladen();
            case 2:
                return new fragZiele();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
