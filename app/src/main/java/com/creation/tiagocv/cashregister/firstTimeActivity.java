package com.creation.tiagocv.cashregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class firstTimeActivity extends AppCompatActivity {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Mostrar seta no canto superior esquerdo
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView name = (TextView) findViewById(R.id.name_textView);
        name.setText(user.getDisplayName() + "!");

        Button create = (Button) findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(firstTimeActivity.this)
                        .title("Create New Shop")
                        .customView(R.layout.dialog_create_shop, true)
                        .negativeText("Cancel")
                        .positiveText("Submit")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText title = (EditText) dialog.getCustomView().findViewById(R.id.create_title);
                                EditText name = (EditText) dialog.getCustomView().findViewById(R.id.create_register);
                                Spinner currency = (Spinner) dialog.getCustomView().findViewById(R.id.create_currency);

                                createShop(title.getText().toString(), currency.getSelectedItem().toString(), name.getText().toString());
                            }
                        })
                        .build();

                // AQUI: código

                dialog.show();
            }
        });

        Button join = (Button) findViewById(R.id.join_button);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog2 = new MaterialDialog.Builder(firstTimeActivity.this)
                        .title("Join Existing Shop")
                        .customView(R.layout.dialog_join_shop, true)
                        .negativeText("Cancel")
                        .build();

                TextView email = (TextView) dialog2.getCustomView().findViewById(R.id.email_textView);
                email.setText(user.getEmail());

                dialog2.show();

                waitForInvite();
            }
        });
    }

    void waitForInvite() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Assim que o UID do usuário estiver na lista 'Users' da DB, levar o usuário para MainActivity
                if (dataSnapshot.hasChild(user.getUid())) {
                    setupSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        rootRef.addValueEventListener(postListener);
    }

    // Fazer signOut e voltar à classe Auth quando o usuário clica na seta do canto superior esquerdo
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(firstTimeActivity.this, Auth.class));
                        finish();
                    }
                });
        return true;
    }

    // Criar entradas relevantes na DB
    void createShop(String title, String currency, String name) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Register newRegister = new Register(1, name, 0, 0, 0);
        // Por agora não escrevemos os nodes Statistics e Items na DB
        Shop newShop = new Shop(null, user.getUid(), title, null, null, null, currency);

        DatabaseReference shopRef = mDatabase.child("Shops").push();
        shopRef.setValue(newShop);
        // Adicionar o dono à lista de registers
        shopRef.child("registers").child(user.getUid()).setValue(newRegister);

        // Adicionar dono à lista de Users
        mDatabase.child("Users").child(user.getUid()).setValue(shopRef.getKey());

        setupSuccess();
    }

    // Redireciona para a classe MainActivity
    void setupSuccess() {
        startActivity(new Intent(firstTimeActivity.this, MainActivity.class));
        finish();
    }

}
