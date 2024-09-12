package com.example.myapplication;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button btnLogout;
    SharedPreferenceClass sharedPreferenceClass;
    DrawerLayout drawerLayout;


    @Override
    protected void onStart() {
        super.onStart();
        String token = sharedPreferenceClass.getValue_string("token");
        String email = sharedPreferenceClass.getValue_string("email");
        String name = sharedPreferenceClass.getValue_string("name");
        String role = sharedPreferenceClass.getValue_string("role");
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(role)) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sharedPreferenceClass = new SharedPreferenceClass(this);
        String email = sharedPreferenceClass.getValue_string("email");
        String name = sharedPreferenceClass.getValue_string("name");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        String userRole = sharedPreferenceClass.getValue_string("role");
        if (userRole.equals("TEACHER")) {
            menu.findItem(R.id.nav_home).setVisible(false);
            menu.findItem(R.id.nav_vocabulary).setVisible(false);  // Ví dụ: chỉ hiện bài tập cho học sinh
            menu.findItem(R.id.nav_grammar).setVisible(false);
            menu.findItem(R.id.nav_exercise).setVisible(false);
            menu.findItem(R.id.nav_help).setVisible(false);// Ví dụ: chỉ hiện bài tập cho học sinh
        } else if (userRole.equals("STUDENT")) {
            menu.findItem(R.id.nav_teacher_help).setVisible(false);

        }
        navigationView.invalidate();
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.account_name);
        TextView emailText = headerView.findViewById(R.id.account_email);
        usernameText.setText(name);
        emailText.setText(email);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            if (userRole.equals("TEACHER")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TeacherChatFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_teacher_help);
            } else if (userRole.equals("STUDENT")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, new OnBackInvokedCallback() {
                        @Override
                        public void onBackInvoked() {
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            } else {
                                finish(); // Or other back behavior
                            }
                        }
                    });
        } else {
            // Handle back press for older APIs
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        // Default back behavior
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_vocabulary) {
            selectedFragment = new VocabularyFragment();
        } else if (id == R.id.nav_grammar) {
            selectedFragment = new GrammarFragment();
        } else if (id == R.id.nav_exercise) {
            selectedFragment = new ExerciseFragment();
        } else if (id == R.id.nav_help) {
            selectedFragment = new StudentChatFragment();
        } else if (id == R.id.nav_logout) {
            sharedPreferenceClass.clear();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}