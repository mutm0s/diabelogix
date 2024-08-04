package com.example.diabetesdetector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.diabetesdetector.ui.bot.ChatBot;
import com.example.diabetesdetector.ui.carbinsulin.FoodEntryFragment;
import com.example.diabetesdetector.ui.dashboard.DailyDashboardFragment;
import com.example.diabetesdetector.ui.dashboard.DashboardFragment;
import com.example.diabetesdetector.ui.diet.DietaryLogFragment;
import com.example.diabetesdetector.ui.home.HomeFragment;
import com.example.diabetesdetector.ui.notifications.NotificationsFragment;
import com.example.diabetesdetector.ui.preferences.preferenceFragment;
import com.example.diabetesdetector.ui.profile.ProfileFragment;
import com.example.diabetesdetector.ui.appointment.DoctorsListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.diabetesdetector.ui.bot.ChatBot;
import com.example.diabetesdetector.ui.carbinsulin.FoodEntryFragment;
import com.example.diabetesdetector.ui.dashboard.DailyDashboardFragment;
import com.example.diabetesdetector.ui.dashboard.DashboardFragment;
import com.example.diabetesdetector.ui.diet.DietaryLogFragment;
import com.example.diabetesdetector.ui.home.HomeFragment;
import com.example.diabetesdetector.ui.notifications.NotificationsFragment;
import com.example.diabetesdetector.ui.preferences.preferenceFragment;
import com.example.diabetesdetector.ui.profile.ProfileFragment;
import com.example.diabetesdetector.ui.appointment.DoctorsListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.toolbar_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_doctor) {
                            Intent intent = new Intent(MainActivity.this, AddDoctorActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (id == R.id.action_predict) {
                            openFragment(new HomeFragment());
                            return true;
                        } else if (id == R.id.action_carbs) {
                            openFragment(new FoodEntryFragment());
                            return true;
                        } else if (id == R.id.action_diet) {
                            openFragment(new DietaryLogFragment());
                            return true;
                        } else if (id == R.id.action_preference) {
                            openFragment(new preferenceFragment());
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);

        // Floating Action Button click listener
        fab.setOnClickListener(view -> {
            // Create an instance of the ChatBot fragment
            ChatBot chatBotFragment = new ChatBot();

            // Add the fragment to the activity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatBotFragment)
                    .addToBackStack(null) // Optional, to allow the user to navigate back
                    .commit();
        });

        // Bottom Navigation Bar click listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_nav_home) {
                openFragment(new DashboardFragment());
                return true;
            } else if (itemId == R.id.bottom_nav_dashboard) {
                openFragment(new DailyDashboardFragment());
                return true;
            } else if (itemId == R.id.bottom_nav_appointments) {
                openFragment(new DoctorsListFragment());
                return true;
            } else if (itemId == R.id.bottom_nav_notifications) {
                openFragment(new NotificationsFragment());
                return true;
            } else if (itemId == R.id.bottom_nav_profile) {
                openFragment(new ProfileFragment());
                return true;
            } else {
                return false;
            }
        });

        // Load the HomeFragment by default
        if (savedInstanceState == null) {
            openFragment(new DashboardFragment());
        }
    }

    // Method to open a fragment
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


