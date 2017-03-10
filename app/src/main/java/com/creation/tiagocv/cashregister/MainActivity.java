package com.creation.tiagocv.cashregister;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Transactions");
        setSupportActionBar(toolbar);


        // Ativa a funcionalidade offline da firebase database
        // O boolean calledAlready é necessário pois se a app voltar a correr esta linha na mesma sessão, crasha
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        // Inicialização do hamburger menu - gerado automaticamente
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(R.id.registerEmailTextView);

        // Se o utilizador não estiver com sesão iniciada, redirecionar para a classe Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();

            // TextView teste: Mostra o nome do usuário
            //TextView test = (TextView) findViewById(R.id.test_textview);
            //test.setText("Hello, " + user.getDisplayName() + "!");

            // Buscar a View dentro do hamburger menu
            View hView =  navigationView.getHeaderView(0);

            // Dentro do hamburger menu, defenir uma das TextViews para mostrar email do usuário
            TextView email = (TextView) hView.findViewById(R.id.registerEmailTextView);
            email.setText(user.getEmail());
        } else {
            Intent myIntent = new Intent(this, Auth.class);
            this.startActivity(myIntent);
            finish();
        }

        // Buscar os dados do usuário
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        // Buscar a View dentro do hamburger menu
        View hView =  navigationView.getHeaderView(0);

        // Links para TextViews dentro do hamburger menu
        final TextView title = (TextView) hView.findViewById(R.id.registerShopTextView);
        final TextView name = (TextView) hView.findViewById(R.id.registerNameTextVIew);

        // Ligar à firebase database

        // Primeiro, ir à DB verificar se já há informação para este usuário ou se ele é novo
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Se a lista de users na DB não tiver uma "child" igual ao ID deste usuário, então este usuário é novo e ainda não tem informação na DB
                if (!(snapshot.hasChild(user.getUid()))) {
                    // firstTimeSetup();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // Por agora retiramos a informação da loja exemplo apenas
        ref = ref.child("Shops").child("-KeGnV-utAaWIwGweqBI");
        final DatabaseReference regRef = ref.child("registers").child(user.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shop shop_class = dataSnapshot.getValue(Shop.class);

                // Dentro do hamburger menu, defenir uma das TextViews para mostrar o título da Shop
                title.setText(shop_class.getTitle());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        ref.addValueEventListener(postListener);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Register register_class = dataSnapshot.getValue(Register.class);

                Log.d("DEBUG", "ID: " + user.getUid() + ", ref: " + regRef.toString());
                Log.d("DEBUG", "Object: " + dataSnapshot.toString());

                // Dentro do hamburger menu, defenir uma das TextViews para mostrar o nome da caixa
                name.setText(register_class.getRegisterName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        regRef.addValueEventListener(postListener2);

        MenuItem addItem = (MenuItem) navigationView.getMenu().getItem(0);
        onNavigationItemSelected(addItem);


    }

    // Ainda não está pronto - Estou a trabalhar nisso
    /**private void firstTimeSetup() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Adicionar Nova Aposta")
                .customView(R.layout.dialog_setupShop, true)
                .negativeText("Cancelar")
                .build();


        Button create = (Button) dialog.getCustomView().findViewById(R.id.createShop);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog2 = new MaterialDialog.Builder(MainActivity.this)
                        .title("Adicionar Nova Aposta")
                        .customView(R.layout.dialog_createShop, true)
                        .negativeText("Cancelar")
                        .build();

                // AQUI: código

                dialog2.show();
            }
        });

        Button join = (Button) dialog.getCustomView().findViewById(R.id.joinShop);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog2 = new MaterialDialog.Builder(MainActivity.this)
                        .title("Adicionar Nova Aposta")
                        .customView(R.layout.dialog_joinShop, true)
                        .negativeText("Cancelar")
                        .build();

                // AQUI: código

                dialog2.show();
            }
        });


        dialog.show();
    }**/

    // SignOut e rederecionar para a classe Auth
    void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, Auth.class));
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Ações a fazer para cada um dos botões do hamburger menu
        if (id == R.id.nav_logOut) {
            signOut();
        } else if (id == R.id.nav_add) {
            // Create a new fragment
            Fragment fragment = new AddFragment();

            getSupportActionBar().setTitle("Add Transaction");

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (id == R.id.nav_registers) {
            // Create a new fragment
            Fragment fragment = new RegistersFragment();

            getSupportActionBar().setTitle("Registers");

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } /**else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }**/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
