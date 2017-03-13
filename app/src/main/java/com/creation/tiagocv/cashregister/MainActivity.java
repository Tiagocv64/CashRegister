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

    /** !!!!!!!!!!!!!! TIAGO LÊ ISTO !!!!!!!!!!!!!!
     *
     * Não sei o que já sabes sobre escrever e ler da DB do firebase portanto pus notas em quase tudo. Caga no que já sabias. A documentação na net é muito boa. No site da Firebase especialmente
     *
     * Se fores fazer uma das listas que é preciso (histórico, items, registers)
     * Lê isto: https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
     * Eu usei na app do PB. É simples. Usa uma RecyclerView, é a ultima parte do texto. Mas lê tudo
     *
     * A maior parte dos Logs já não são precisos. Eu é que tive preguiça de tirar.
     *
     **/

    static boolean calledAlready = false;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final boolean[] firstTime = {true};

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Loading...");
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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(R.id.registerEmailTextView);

        // Se o utilizador não estiver com sesão iniciada, redirecionar para a classe Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();

            Log.d("DEBUG", "Logged in as: " + user.getDisplayName() + " , UID: " + user.getUid());

            // TextView teste: Mostra o nome do usuário
            //TextView test = (TextView) findViewById(R.id.test_textview);
            //test.setText("Hello, " + user.getDisplayName() + "!");

            // Buscar a View dentro do hamburger menu
            View hView = navigationView.getHeaderView(0);

            // Dentro do hamburger menu, defenir uma das TextViews para mostrar email do usuário
            TextView email = (TextView) hView.findViewById(R.id.registerEmailTextView);
            email.setText(user.getEmail());
        } else {
            Intent myIntent = new Intent(this, Auth.class);
            this.startActivity(myIntent);
            finish();
        }

        // Buscar os dados do usuário
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        // Buscar a View dentro do hamburger menu
        View hView = navigationView.getHeaderView(0);

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
                    firstTimeSetup();
                } else {
                    // Else, executar lógica principal
                    Log.d("DEBUG", "Entering mainLogic, shopID is: " + snapshot.child(user.getUid()).getValue().toString());
                    mainLogic(user, name, title, snapshot.child(user.getUid()).getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        });

    }

    // Lógica Principal: Preencher campos no hamburger menu de acordo com a DB
    void mainLogic(final FirebaseUser user, final TextView name, final TextView title, String shopID) {
        // Vamos buscar 2 Dados à DB: /Shops/$shopID/title (nome da loja) e /Shops/$shopID/registers/registerName (nome da caixa)

        // DatabaseReference é o caminho/link da DB. Exemplo: https://cashregister-eeb90.firebaseio.com/Shops/-KeGnV-utAaWIwGweqBI/registers/0D04Rxm1itdMzB6v3yHmNm0jYGo1/
        // Neste caso ref é na raiz da DB
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // ref passa a ser em /Shops/$shopID
        ref = ref.child("Shops").child(shopID);

        // Este postListener abre uma especie de nova thread que está sempre a verficar se algo mudou numa determinada referncia/caminho da DB
        // Para perceber melhor LER: https://firebase.google.com/docs/database/android/read-and-write
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // VER A CLASSE Shop.java EM DatabaseClasses.java
                // Isto pega nos dados que estão em /Shops/$shopID (ref) na DB e inicializa uma objeto da classe Shop com eles. Não há outra maneira de ler dados da DB do firebase
                Shop shop_class = dataSnapshot.getValue(Shop.class);

                Log.d("DEBUG", "Loja: " + shop_class.getTitle());
                // Dentro do hamburger menu, defenir uma das TextViews para mostrar o título da Shop
                title.setText(shop_class.getTitle());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        // Adicona o postListener à referência ref
        ref.addValueEventListener(postListener);

        // Criar uma nova ref: 'regRef' em /Shops/$shopID/registers/$uid
        final DatabaseReference regRef = ref.child("registers").child(user.getUid());

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

    // Se o user não tive loja, levá-lo ao setup
    private void firstTimeSetup(){
        startActivity(new Intent(MainActivity.this, firstTimeActivity.class));
        finish();
    }

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
        } else if (id == R.id.nav_history){
            // Create a new fragment
            Fragment fragment = new HistoryFragment();

            getSupportActionBar().setTitle("History");

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (id == R.id.nav_statistics){
            // Create a new fragment
            Fragment fragment = new StatisticsFragment();

            getSupportActionBar().setTitle("Statistics");

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (id == R.id.nav_items){
            // Create a new fragment
            Fragment fragment = new ItemsFragment();

            getSupportActionBar().setTitle("Items");

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
        }

        /**else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }**/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
