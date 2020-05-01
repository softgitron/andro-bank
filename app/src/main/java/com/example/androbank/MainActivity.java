package com.example.androbank;

import android.os.Bundle;

import com.example.androbank.session.Session;

import androidx.navigation.NavController;

import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
        navController.setGraph(graph);

        toolbar.setNavigationOnClickListener(v -> {
            //System.out.println(v.getId() );
            navController.navigateUp();
            // navController.popBackStack();
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // SOURCE: https://stackoverflow.com/a/53756341
        // setting title according to fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            toolbar.setTitle(navController.getCurrentDestination().getLabel() );
            // Change virtual back arrow status based on the layout
            if (destination.getId() == R.id.main_Menu || destination.getId() == R.id.nav_home) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Do not save session if we are at specific page
        if (navController.getCurrentDestination().getId() == R.id.passcode) {
            return;
        }
        Session.getSession().sessionDump(getBaseContext());
    }


}
