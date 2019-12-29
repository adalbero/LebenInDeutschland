package com.adalbero.app.lebenindeutschland.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Adalbero on 01/06/2017.
 */

public class Dialog {
    public static void promptDialog(Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text)
                .setPositiveButton("OK", null);
        builder.show();
    }

    public static void appNotFoundDialog(final Context context, final String text, final String pack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text + " not found");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Install", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("market://details?id=" + pack));
                    context.startActivity(goToMarket);
                } catch (Exception ex) {
                    Toast.makeText(context, "Fail to install " + text, Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.show();
    }

}
