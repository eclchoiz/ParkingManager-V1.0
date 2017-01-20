package com.eclchoiz.example.parkingmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Eclchoiz on 2017-01-03.
 */

public class MmsDialogFragment extends DialogFragment {

    NoticeDialogListener mListener;
    int mSelectedItem;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int id);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.mms_select);
        builder.setSingleChoiceItems(R.array.mms_array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                mSelectedItem = id;
            }
        })
                .setNegativeButton(R.string.mms_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        mListener.onDialogNegativeClick(MmsDialogFragment.this);
                    }
                })
                .setPositiveButton(R.string.mms_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        mListener.onDialogPositiveClick(mSelectedItem);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }
}
