package com.sam_chordas.android.stockhawk.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

/**
 * @author Michael Vescovo.
 */

public class Util {

    public static Snackbar networkSnackbar(Activity activity, boolean isConnected, Snackbar snackbar, View view) {
        if (isConnected) {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        } else {
            snackbar = Snackbar.make(view, R.string.network_snackbar, Snackbar.LENGTH_INDEFINITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(activity, R.color.black));
            snackbar.show();
        }
        return snackbar;
    }
}
