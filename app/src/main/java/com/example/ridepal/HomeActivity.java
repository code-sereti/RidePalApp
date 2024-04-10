package com.example.ridepal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
//    RidesFragment ridesFragment = new RidesFragment();
//    RideHistoryFragment rideHistoryFragment=new RideHistoryFragment();
//    HelpFragment helpFragment=new HelpFragment();
//    GroupRideFragment groupRideFragment = new GroupRideFragment();
    CreateRideFragment createRideFragment = new CreateRideFragment();
    CreateGroupRideFragment createGroupRideFragment = new CreateGroupRideFragment();

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.navigation_menu_open,
                R.string.navigation_menu_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId=item.getItemId();
                if (itemId== R.id.home){
                    openFragment(new Home());
                } else if (itemId == R.id.create) {
                    openFragment(new CreateRideFragment());
                } else if (itemId == R.id.create_group_ride) {
                    openFragment(new CreateGroupRideFragment());
                } else if (itemId == R.id.ride_history) {
                    openFragment(new RideHistoryFragment());
                }

                return false;
            }
        });
        fragmentManager=getSupportFragmentManager();
        openFragment(new Home());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if (itemId== R.id.nav_account) {
            openFragment(new SettingFragment());
        } else if (itemId == R.id.help) {
            openFragment(new HelpFragment());

        } else if (itemId== R.id.nav_logout) {
            openFragment(new LogOutFragment());

        }else if (itemId == R.id.nav_settings) {
            Intent intent = new Intent(this, AccountSettings.class);
            startActivity(intent);
        } else if (itemId==R.id.nav_verification) {
            Intent intent = new Intent(this, VerificationActivity.class);
            startActivity(intent);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    private void openFragment(Fragment fragment){
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}