package com.creation.tiagocv.cashregister;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddFragment extends Fragment {

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add, container, false);
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //toolbar.setTitle("Add Transactions");
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = { "Lista", "ID", "Scanner"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Adding Method");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        //Codigo do que acontece ao clicar nos items

                        dialog.dismiss();

                    }
                }).show();
            }
        });

        Log.d("DEBUG", "Entered AddFragment!");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // TextView teste: Mostra o nome do usuário
        TextView test = (TextView) view.findViewById(R.id.test_textview);
        test.setText("Hello, " + user.getDisplayName() + "!");

        return view;
    }

}
