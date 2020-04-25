package com.example.androbank;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.androbank.session.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);

        // Load the previous session if possible.
        Session.getSession().sessionLoad(getBaseContext());
        if (Session.getSession().user.getEmail() != null) {
            graph.setStartDestination(R.id.main_Menu);
            // navController.navigate(R.id.main_Menu);
        } else {
            graph.setStartDestination(R.id.nav_home);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session.getSession().sessionDump(getBaseContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
