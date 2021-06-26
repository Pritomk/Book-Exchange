package com.example.bookexchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

import static com.example.bookexchange.util.constant.appID;
import static com.example.bookexchange.util.constant.genres;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private App app;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    Spinner mainSpinner,genreSpinnerList;
    ArrayAdapter<String> arrayAdapterHome;
    private final String TAG = "userdata";
    FloatingActionButton addBtn;
    Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(getApplicationContext());

        //If not logged in the redirect to login activity
        app = new App(new AppConfiguration.Builder(appID).build());
        if (app.currentUser() == null) {
            gotoLogin();
        }

        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mainSpinner = findViewById(R.id.spinnerID);
        addBtn = findViewById(R.id.addBtn);
        signOutBtn = findViewById(R.id.signOutBtn);
        genreSpinnerList = findViewById(R.id.genreSpinnerList);

        addBtn.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,ExchangeActivity.class));
        });

        signOutBtn.setOnClickListener(v->signOut());

        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_list)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Call spinner method
        setSpinnerHome();
    }

    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class));
    }

    private void signOut() {
        User user = app.currentUser();
        user.logOutAsync(result -> {
            if (result.isSuccess()) {
                Toast.makeText(this,"Signout successfully",Toast.LENGTH_SHORT).show();
                signOutBtn.setText("Sign in");
                signOutBtn.setOnClickListener(v->gotoLogin());
            }
        });
    }


    //Add navigation button and set search button

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(MainActivity.this,"Expand",Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(MainActivity.this,"Collapsed",Toast.LENGTH_SHORT).show();
                return true;

            }
        };

        menu.findItem(R.id.searchView).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setQueryHint("Search Here");

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // Add spinner
    private void setSpinnerHome() {
        mainSpinner.setOnItemSelectedListener(MainActivity.this);
        arrayAdapterHome = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genres);
        arrayAdapterHome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mainSpinner.setAdapter(arrayAdapterHome);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}