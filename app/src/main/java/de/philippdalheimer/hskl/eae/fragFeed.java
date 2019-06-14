package de.philippdalheimer.hskl.eae;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.philippdalheimer.hskl.eae.classes.Artikel;
import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;

public class fragFeed extends Fragment {

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View ctx = inflater.inflate(R.layout.fragment_feed, container, false);

        listView = ctx.findViewById(R.id.lv_feed);

        //TODO: In diese Liste kommen nachher alle Artikel rein, die von GSON aus der JSON ausgelesen wurden!
        ArrayList<Artikel> artikelListeWg = new ArrayList<>();

        ListViewAdapter listViewAdapter = new ListViewAdapter(getContext(), artikelListeWg);

        listView.setAdapter(listViewAdapter);
        return ctx;
    }
}
