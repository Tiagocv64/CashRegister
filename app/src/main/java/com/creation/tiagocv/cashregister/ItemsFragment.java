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

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final FloatingActionButton fabCategory = (FloatingActionButton) view.findViewById(R.id.fab_category);
        final FloatingActionButton fabItem = (FloatingActionButton) view.findViewById(R.id.fab_item);
        ViewCompat.animate(fab).rotation(-90f).setDuration(0).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pressedFAB) {
                    ViewCompat.animate(fab).
                            rotation(135f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator())
                            .start();
                    ViewCompat.animate(fabCategory).
                            rotation(90f).
                            translationY(-250f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator())
                            .start();
                    ViewCompat.animate(fabItem).
                            rotation(0f).
                            translationY(-450f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator())
                            .start();
                    fabCategory.setClickable(true);
                    fabItem.setClickable(true);
                    pressedFAB = !pressedFAB;
                } else {
                    ViewCompat.animate(fab).
                            rotation(0f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator())
                            .start();
                    ViewCompat.animate(fabCategory).
                            rotation(0f).
                            translationY(0f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator(0f))
                            .start();
                    ViewCompat.animate(fabItem).
                            rotation(-90f).
                            translationY(0f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(new OvershootInterpolator(0f))
                            .start();
                    fabCategory.setClickable(false);
                    fabItem.setClickable(false);

                    pressedFAB = !pressedFAB;
                }
            }
        });

                

        Log.d("DEBUG", "Entered ItemsFragment!");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }
}
