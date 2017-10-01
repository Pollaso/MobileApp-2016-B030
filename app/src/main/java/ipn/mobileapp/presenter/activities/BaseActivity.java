package ipn.mobileapp.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.dao.Database;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.service.dao.user.IUserSchema;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private NavigationView navigationView;
    private LinearLayout navHeader;

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setNavigationView();
        setDrawerLayout();
    }

    private void setDrawerLayout() {
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ImageView ivProfileImage= (ImageView) header.findViewById(R.id.header_profile_picture);
        TextView tvHeaderName = (TextView)header.findViewById(R.id.header_name);
        TextView tvHeaderEmail= (TextView)header.findViewById(R.id.header_email);

        SharedPreferencesManager manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        try {
            final Dao<User, String> userDao = databaseHelper.getUserDao();
            User user = userDao.queryForId((String)manager.getValue("_id", String.class));
            tvHeaderName.setText(user.getName());
            tvHeaderEmail.setText(user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }

        navHeader = (LinearLayout) navigationView.getHeaderView(0);
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
        else {
            if (backPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                finish();
                System.exit(0);
            } else {
                Toast.makeText(BaseActivity.this, "Presione el botón de regresar de nuevo para cerrar la aplicación", Toast.LENGTH_SHORT).show();
            }

            backPressed = System.currentTimeMillis();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        /*android.support.v4.app.Fragment fragment = null;*/

        switch (id) {
            case R.id.nav_menu_home:
                intent = new Intent(getBaseContext(), HomeActivity.class);
                /*fragment = new FragmentOne();
                getSupportActionBar().setTitle("Fragmento Home");*/
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
                logout();
                intent = new Intent(getBaseContext(), MainActivity.class);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        finish();
        startActivity(intent);

        /*if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }*/

        return false;
    }

    public void logout() {
        getApplicationContext().deleteDatabase(getString(R.string.database_name));

        SharedPreferencesManager manager = new SharedPreferencesManager(BaseActivity.this, "currentUser");
        manager.clear();
    }
}

