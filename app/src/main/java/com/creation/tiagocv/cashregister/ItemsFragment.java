package com.creation.tiagocv.cashregister;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by tcver on 10/03/2017.
 */

public class ItemsFragment extends Fragment {

    boolean pressedFAB = false;

    public static ItemsFragment newInstance() {
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

        com.github.clans.fab.FloatingActionButton fabCategory = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab_category);
        com.github.clans.fab.FloatingActionButton fabItem = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab_item);

        fabCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CODE
            }
        });

        fabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CODE
            }
        });

        Log.d("DEBUG", "Entered ItemsFragment!");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }
}
