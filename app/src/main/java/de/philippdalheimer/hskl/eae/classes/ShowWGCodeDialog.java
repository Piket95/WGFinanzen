package de.philippdalheimer.hskl.eae.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.philippdalheimer.hskl.eae.R;

public class ShowWGCodeDialog extends AppCompatDialogFragment {

    TextView[] textViews;
    char[] wgcode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_show_wg_code, null);

        textViews = new TextView[]{view.findViewById(R.id.lbl_wg_code_1), view.findViewById(R.id.lbl_wg_code_2), view.findViewById(R.id.lbl_wg_code_3), view.findViewById(R.id.lbl_wg_code_4), view.findViewById(R.id.lbl_wg_code_5), view.findViewById(R.id.lbl_wg_code_6)};
        wgcode = User.member_info.wg_code.toCharArray();

//        Log.d("TestApp", Integer.toString(wgcode.length));

        for(int i = 0; i < wgcode.length; i++){
            textViews[i].setText(Character.toString(wgcode[i]));
//            Log.d("TestApp", Character.toString(wgcode[i]));
        }

        builder.setView(view)
                .setTitle(getResources().getString(R.string.diaShow_title))
                .setPositiveButton(getResources().getString(R.string.dia_Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Muss nix rein!
                    }
                });

        return builder.create();
    }
}
