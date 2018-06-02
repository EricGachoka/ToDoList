package com.example.enrico.todolist.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.enrico.todolist.R;

public class GiveUpCurrentEditing extends DialogFragment {

    private CancelButtonClick mHost;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.cancel_changes);
        builder.setMessage(R.string.give_up_changes);

        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mHost.refreshFragment();
            }
        });

        builder.setNegativeButton(R.string.no, null);

        return builder.create();
    }

    public interface CancelButtonClick {
        void refreshFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHost = (CancelButtonClick) activity;
    }
}
