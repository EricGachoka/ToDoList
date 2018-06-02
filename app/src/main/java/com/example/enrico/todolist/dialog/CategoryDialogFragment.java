package com.example.enrico.todolist.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.enrico.todolist.R;

public class CategoryDialogFragment extends DialogFragment {

    private CategoryDialogListener mHost;
    private String category;

    public interface CategoryDialogListener{
        void onPositiveResult(String category);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_category_dialog,null);
        final TextView textView = v.findViewById(R.id.categoryText);

        builder.setTitle(R.string.create_category)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        category = textView.getText().toString();
                        mHost.onPositiveResult(category);
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .setView(v);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHost = (CategoryDialogListener) activity;
    }
}
