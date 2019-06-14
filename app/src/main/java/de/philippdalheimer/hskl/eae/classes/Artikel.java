package de.philippdalheimer.hskl.eae.classes;

import java.text.DecimalFormat;

public class Artikel {

    public String id;
    public String name;
    public String beschreibung;
    public String category_id;
    public String datum;
    public String preis;
    public String created;
    public String creator;
    public String category;

    public String getEuro(){

        DecimalFormat df = new DecimalFormat("0");

        return df.format(Double.parseDouble((this.preis)));
    }

    public String getCent(){

        DecimalFormat df = new DecimalFormat("0");

        int euro = Integer.parseInt(getEuro());
        double erg = Double.parseDouble(this.preis);

        erg = erg - euro;

        erg *= 100;

        return ("," + erg);
    }
}
