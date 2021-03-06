package de.philippdalheimer.hskl.eae;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.philippdalheimer.hskl.eae.classes.wg.ShowWGCodeDialog;

public class fragBewohner_einladen extends Fragment {

    View ctx;
    Button btnShowCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ctx = inflater.inflate(R.layout.fragment_frag_bewohner_einladen, container, false);

        //Beim klick auf den Button "WG-Code anzeigen" wird der selbst erstellte Dialog (dialog_show_wg_code.xml) erstellt und angezeigt
        btnShowCode = ctx.findViewById(R.id.btn_show_wg_code);
        btnShowCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowWGCodeDialog dialog = new ShowWGCodeDialog();
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });


        return ctx;
    }
}
