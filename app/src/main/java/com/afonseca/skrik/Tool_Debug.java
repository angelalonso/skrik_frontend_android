package com.afonseca.skrik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by aaf on 3/18/15.
 */
public class Tool_Debug {
    public void popup(Context inContext,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(inContext);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
