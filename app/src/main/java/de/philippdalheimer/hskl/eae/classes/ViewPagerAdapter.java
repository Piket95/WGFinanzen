package de.philippdalheimer.hskl.eae.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.philippdalheimer.hskl.eae.classes.User.User;
import de.philippdalheimer.hskl.eae.fragBewohner_einladen;
import de.philippdalheimer.hskl.eae.fragFeed;
import de.philippdalheimer.hskl.eae.fragWG_Beitreten;
import de.philippdalheimer.hskl.eae.fragZiele;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = {new fragFeed(), new fragBewohner_einladen(), new fragZiele()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //TODO: If Abfrage ob eingeladen werden soll oder beigetreten werden soll!!!

        if(position == 1 && User.member_info.wg_code.equals("-1")){
            return new fragWG_Beitreten();
        }

        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
