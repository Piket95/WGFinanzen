package de.philippdalheimer.hskl.eae.classes;

import android.content.Context;
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
import de.philippdalheimer.hskl.eae.classes.Artikel.Artikel;

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

        ImageView image = listItem.findViewById(R.id.item_image);

        switch (Integer.parseInt(currentArtikel.category_id)){
            case 1:
                image.setImageResource(R.drawable.lebensmittel);
                break;
            case 2:
                image.setImageResource(R.drawable.unterhaltung);
                break;
            case 3:
                image.setImageResource(R.drawable.freizeit);
                break;
            case 4:
                image.setImageResource(R.drawable.haushalt);
                break;
            default:
                break;
        }

        TextView txtName = listItem.findViewById(R.id.txt_item_heading);
        txtName.setText(currentArtikel.name);

        TextView txtCategory = listItem.findViewById(R.id.txt_item_category);
        txtCategory.setText(currentArtikel.category);

        TextView txtEuro = listItem.findViewById(R.id.feed_euro);
        txtEuro.setText(currentArtikel.getPreis()[0]);

        TextView txtCent = listItem.findViewById(R.id.feed_cent);
        txtCent.setText(", " + currentArtikel.getPreis()[1] + " €");

        return listItem;
    }
}
