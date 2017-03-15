package com.creation.tiagocv.cashregister;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class
RegistersFragment extends Fragment {

    ArrayList<String> RegistersList = new ArrayList<String>();
    View view;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static RegistersFragment newInstance() {
        RegistersFragment fragment = new RegistersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_registers, container, false);
        // qfinal ListView listView = (ListView) view.findViewById(R.id.list_registers);  //ListView com o nome das caixas

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
                        .title("Add New Register")
                        .customView(R.layout.dialog_invite_register, true)
                        .negativeText("Cancel")
                        .positiveText("Submit")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText email = (EditText) dialog.getCustomView().findViewById(R.id.email_editText);
                                EditText name = (EditText) dialog.getCustomView().findViewById(R.id.register_editText);

                                preInviteRegister(email.getText().toString(), name.getText().toString());
                            }
                        })
                        .build();

                // AQUI: código

                dialog.show();
            }
        });


        Log.d("DEBUG", "Entered RegistersFragment!");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()); //Reference ao utilizador


        //Buscar os nomes para a RegistersList e popular a ListView
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userShop = dataSnapshot.getValue(String.class);
                Log.e("Log", "UserShop: " +userShop);
                DatabaseReference refRegisters = FirebaseDatabase.getInstance().getReference().child("Shops").child(userShop).child("registers");

                refRegisters.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> RegistersList = new ArrayList<String>();
                        // Result will be holded Here
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            RegistersList.add(String.valueOf(dsp.child("registerName").getValue())); //adicionar os nomes a ArrayList
                        }
                        //Popular a ListView
                       // ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, RegistersList);
                        //listView.setAdapter(arrayAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("Register", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Register", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        reference.addValueEventListener(postListener);


        GridLayout gridRegisters = (GridLayout) view.findViewById(R.id.root_grid_layout);
        gridRegisters.setColumnCount(2);



        return view;
    }

    // Buscar o ID da shop onde o user está para que o novo user possa ser convidado
    void preInviteRegister(final String email, final String name) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                inviteRegister(email, name, snapshot.child(user.getUid()).getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        });
    }

    // Adicionar as entradas relevantes à DB para que o novo uer seja convidado
    void inviteRegister(String email, String name, String shopID) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Pending").child(shopID).child(name).setValue(email);

        Snackbar.make(view, "Your new Register will be added momentarily", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
