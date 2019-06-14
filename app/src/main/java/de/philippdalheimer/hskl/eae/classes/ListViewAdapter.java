package de.philippdalheimer.hskl.eae.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.philippdalheimer.hskl.eae.R;

public class ListViewAdapter extends ArrayAdapter<Artikel> {

    private Context context;
    private ArrayList<Artikel> wgArtikelListe;

    public ListViewAdapter(@NonNull Context context, @NonNull ArrayList<Artikel> list) {
        super(context,0, list);

        this.context = context;
        wgArtikelListe = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.main_feed_row_item, parent, false);
        }

        Artikel currentArtikel = wgArtikelListe.get(position);

        //TODO: Image setzen in ListView pro Artikel!
//        ImageView image = listItem.findViewById(R.id.item_image);
//        image.setImageResource(currentArtikel.getImage);

        TextView txtName = listItem.findViewById(R.id.txt_item_heading);
        txtName.setText(currentArtikel.name);

        TextView txtCategory = listItem.findViewById(R.id.txt_item_category);
        txtName.setText(currentArtikel.category);

        TextView txtEuro = listItem.findViewById(R.id.feed_euro);
        txtName.setText(currentArtikel.getEuro());

        TextView txtCent = listItem.findViewById(R.id.feed_cent);
        txtName.setText(currentArtikel.getCent());

        return super.getView(position, convertView, parent);
    }
}
