package com.stho.mayai;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        final AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                    R.id.navigation_home,
                    R.id.navigation_alarms,
                    R.id.navigation_settings)
                .build();
        final NavController navController = findNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        viewModel = MainViewModel.build(this);
        MayaiNotificationManager.build(this).registerNotificationChannel();
        onNewIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Alarm alarm = Helpers.getAlarmFromIntent(intent);
        if (alarm != null) {
            final String action = intent.getAction();
            if (Helpers.isDetails(action)) {
                navigateToCountdown(alarm);
            }
            // Mind, the actual fired alarm intent is not routed to this main activity, but to the target activity
        } else {
            if (viewModel.hasUnfinishedAlarms()) {
                MayaiWorker.build(this).initialize();
                navigateToAlarms();
            }
        }
    }

    private void navigateToCountdown(final @NonNull Alarm alarm ) {
        findNavController().navigate(
                MobileNavigationDirections.actionGlobalNavigationAlarmCountdown()
                        .setAlarm(alarm.serialize()));
    }

    private void navigateToAlarms() {
        findNavController().navigate(
                MobileNavigationDirections.actionGlobalNavigationAlarms());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            findNavController().navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private @NonNull NavController findNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }
}
