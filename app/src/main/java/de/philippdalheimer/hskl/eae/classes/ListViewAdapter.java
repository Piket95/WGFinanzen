package de.philippdalheimer.hskl.eae.classes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.philippdalheimer.hskl.eae.R;

//TODO: Von komplett falscher Klasse geerbt (Hier kein CursorAdapter verwenden!!! NEUE ÜBERLEGUNG! ArrayAdapter!?!?
public class ListViewAdapter extends CursorAdapter {

    LayoutInflater layoutInflater;
    int itemLayout;


    public ListViewAdapter(Context context,int itemLayout, Cursor c, int flags) {
        super(context, c, flags);
        layoutInflater = LayoutInflater.from(context);
        this.itemLayout = itemLayout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = layoutInflater.inflate(itemLayout, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //ImageView


        //TODO: IDs könnten sich geändert haben!
//        String strTitleHeading = Titel aus Datenbank hier einfügen
//        TextView txtTitleHeading = view.findViewById(R.id.txt_title_heading);
//        txtTitleHeading.setText(strTitleHeading);

        //Txt Kategorie

        //Txt Euro
//        String strEuro = Euro Betrag aus Datenbank
        TextView txtEuro = view.findViewById(R.id.feed_euro);
        txtEuro.setText(strEuro);

        //Txt Cent
//        String strCent = Cent Betrag aus Datenbank
        TextView txtCent = view.findViewById(R.id.feed_cent);
        txtCent.setText(strCent);
    }
}
