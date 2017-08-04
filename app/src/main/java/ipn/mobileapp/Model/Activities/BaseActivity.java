package ipn.mobileapp.Model.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ipn.mobileapp.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private LinearLayout navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setNavigation();
        setDrawerLayout();
    }

    private void setDrawerLayout() {
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
//        android.support.v4.app.Fragment fragment = null;

        switch (id) {
            case R.id.nav_menu_home:
                intent = new Intent(getBaseContext(), HomeActivity.class);
//                fragment = new FragmentOne();
//                getSupportActionBar().setTitle("Fragmento Home");
                break;
            case R.id.nav_menu_profile:
                intent = new Intent(getBaseContext(), ProfileActivity.class);
                break;
            case R.id.nav_menu_sub_users:
                intent = new Intent(getBaseContext(), SubUsersActivity.class);
                break;
            case R.id.nav_menu_contacts:
                intent = new Intent(getBaseContext(), ContactsActivity.class);
                break;
            case R.id.nav_menu_alerts:
                intent = new Intent(getBaseContext(), AlertsActivity.class);
                break;
            case R.id.nav_menu_about_us:
                intent = new Intent(getBaseContext(), HomeActivity.class);
                break;
            case R.id.nav_menu_privacy:
                intent = new Intent(getBaseContext(), HomeActivity.class);
                break;
            case R.id.nav_menu_logout:
                intent = new Intent(getBaseContext(), MainActivity.class);
                break;
        }

        startActivity(intent);

//        if (fragment != null) {
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//        }
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }
}

