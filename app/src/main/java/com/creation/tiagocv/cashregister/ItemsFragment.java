package com.creation.tiagocv.cashregister;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by tcver on 10/03/2017.
 */

public class ItemsFragment extends Fragment {
    public static ItemsFragment newInstance(){
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_items, container, false);
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //toolbar.setTitle("Add Transactions");
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Log.d("DEBUG", "Entered ItemsFragment!");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }
}
