package com.example.androbank;

import android.os.Bundle;
import android.view.View;

import com.example.androbank.session.Session;

import androidx.navigation.NavController;

import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

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
        navController.setGraph(graph);

        // Todo back button is back, but is bugged!
        toolbar.setNavigationOnClickListener(v -> {
            //System.out.println(v.getId() );
            navController.popBackStack();
        });

        // SOURCE: https://stackoverflow.com/a/53756341
        // setting title according to fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            toolbar.setTitle(navController.getCurrentDestination().getLabel() );
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session.getSession().sessionDump(getBaseContext());
    }


}
