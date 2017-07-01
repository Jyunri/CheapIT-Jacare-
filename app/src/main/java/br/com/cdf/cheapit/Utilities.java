package br.com.cdf.cheapit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Jimy on 01/07/17.
 */

public class Utilities {
    public static void customDialog(Context context, final String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        //builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (title){
                            case "Ligar":

                                break;
                            case "Facebook":

                                break;
                        }
                    }
                });
        builder.setNegativeButton(
                "Nao",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (title){
                            case "Ligar": dialog.cancel(); break;
                            case "Facebook": dialog.cancel(); break;
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
