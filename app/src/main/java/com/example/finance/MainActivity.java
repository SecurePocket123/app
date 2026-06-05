package com.example.finance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.finance.app.AppContainer;
import com.example.finance.app.DemoDataProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContainer.init(getApplicationContext());
        DemoDataProvider.seed(AppContainer.getRepository());
        setContentView(R.layout.startseite);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}
