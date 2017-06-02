package com.adalbero.app.lebenindeutschland.controller;

import android.content.Context;
import android.support.v7.app.AlertDialog;

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
}
