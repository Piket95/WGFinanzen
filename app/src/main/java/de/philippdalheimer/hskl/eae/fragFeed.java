package de.philippdalheimer.hskl.eae;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.philippdalheimer.hskl.eae.classes.ListViewAdapter;

public class fragFeed extends Fragment {

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View ctx = inflater.inflate(R.layout.fragment_feed, container, false);

        listView = ctx.findViewById(R.id.lv_feed);
        int itemLayout = R.layout.main_feed_row_item;

        ListViewAdapter listViewAdapter = new ListViewAdapter(ctx, listView, null, 0);

        listView.setAdapter(listViewAdapter);

        return ctx;
    }
}
