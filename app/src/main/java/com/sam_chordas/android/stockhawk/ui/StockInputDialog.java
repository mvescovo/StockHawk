package com.sam_chordas.android.stockhawk.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.sam_chordas.android.stockhawk.R;


/**
 * @author Michael Vescovo.
 */

public class StockInputDialog extends DialogFragment {

    StockInputDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (StockInputDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement StockInputDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_input_stock, null))
                .setPositiveButton(R.string.add_stock_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(StockInputDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel_stock_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(StockInputDialog.this);
                    }
                });
        return builder.create();
    }

    public interface StockInputDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
