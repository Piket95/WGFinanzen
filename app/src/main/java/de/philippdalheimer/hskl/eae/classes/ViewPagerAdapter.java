package de.philippdalheimer.hskl.eae.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.philippdalheimer.hskl.eae.classes.user.User;
import de.philippdalheimer.hskl.eae.fragBewohner_einladen;
import de.philippdalheimer.hskl.eae.fragFeed;
import de.philippdalheimer.hskl.eae.fragWG_Beitreten;
import de.philippdalheimer.hskl.eae.fragZiele;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //Klasse um das Swipen zwischen den 3 Fragmenten (Feed, WG beitreten/einladen und Ziele) zu realisieren
    //Adapter für die ListView Komponente auf dem Layout der Main Activity
    Fragment[] fragments = {new fragFeed(), new fragBewohner_einladen(), new fragZiele()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //Je nachdem auf welcher Position der ViewPager steht, wird dementsprechend das zugehörige Fragment geladen
    @Override
    public Fragment getItem(int position) {

        //Steht der ViewPager auf position 1 und es ist kein wgcode für den aktuellen user hinterlegt, wird das Fragment WG beitreten geladen
        //andernfalls (also wenn wgcode vorhanden ist), wird das Fragment Bewohner einladen geladen und im ViewPager angezeigt
        if(position == 1 && User.member_info.wg_code.equals("-1")){
            return new fragWG_Beitreten();
        }

        return fragments[position];
    }

    //Anzahl der Fragmente, welche die BottomNavigation beinhalten soll (hier 3 Fragmente)
    @Override
    public int getCount() {
        return fragments.length;
    }
}
