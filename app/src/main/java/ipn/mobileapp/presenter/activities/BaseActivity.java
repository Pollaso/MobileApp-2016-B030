package ipn.mobileapp.presenter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Contact;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.service.dao.user.IUserSchema;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private NavigationView navigationView;
    private LinearLayout navHeader;
    private Menu navMenu;

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    protected static User user;

    protected static String id;
    protected static String userId;
    protected static boolean isSubUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        SharedPreferencesManager manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        if (id == null)
            id = (String) manager.getValue("id", String.class);
        if (user == null)
            getUser();
        if (isSubUser && userId == null) {
            manager = new SharedPreferencesManager(BaseActivity.this, "currentSupervisor");
            userId = (String) manager.getValue("userId", String.class);
        }

        setNavigationView();
        setDrawerLayout();
    }

    protected void getUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        try {
            final Dao<User, String> userDao = databaseHelper.getUserDao();
            user = userDao.queryForId(id);
            if (user.getRole().equals(User.SUBUSER_ROLE))
                isSubUser = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }
    }

    protected void setUser(User user) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        try {
            final Dao<User, String> userDao = databaseHelper.getUserDao();
            UpdateBuilder<User, String> updateBuilder = userDao.updateBuilder();
            updateBuilder.updateColumnValue(IUserSchema.COLUMN_EMAIL, user.getEmail());
            updateBuilder.updateColumnValue(IUserSchema.COLUMN_NAME, user.getName());
            updateBuilder.updateColumnValue(IUserSchema.COLUMN_PATERNAL_SURNAME, user.getPaternalSurname());
            updateBuilder.updateColumnValue(IUserSchema.COLUMN_MATERNAL_SURNAME, user.getMaternalSurname());
            updateBuilder.updateColumnValue(IUserSchema.COLUMN_BIRTHDATE, user.getBirthdate());
            updateBuilder.where().eq(IUserSchema.COLUMN_ID, id);
            updateBuilder.update();
            this.user = user;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }
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
        ImageView ivProfileImage = (ImageView) header.findViewById(R.id.header_profile_picture);
        TextView tvHeaderName = (TextView) header.findViewById(R.id.header_name);
        TextView tvHeaderEmail = (TextView) header.findViewById(R.id.header_email);
        tvHeaderName.setText(user.getName());
        tvHeaderEmail.setText(user.getEmail());

        navHeader = (LinearLayout) navigationView.getHeaderView(0);
        navMenu = navigationView.getMenu();
        MenuItem menuItem = navMenu.getItem(getResources().getInteger(R.integer.sub_user_menu_item_index));
        if (user.getRole().equals(User.SUBUSER_ROLE))
            menuItem.setVisible(false);
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

        boolean finish = true;
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
            case R.id.nav_menu_vehicles:
                intent = new Intent(getBaseContext(), VehiclesActivity.class);
                break;
            case R.id.nav_menu_contacts:
                intent = new Intent(getBaseContext(), ContactsActivity.class);
                break;
            case R.id.nav_menu_alerts:
                intent = new Intent(getBaseContext(), AlertsActivity.class);
                break;
            case R.id.nav_menu_about_us:
                finish = false;
                intent = new Intent(getBaseContext(), HomeActivity.class);
                break;
            case R.id.nav_menu_privacy:
                finish = false;
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.localhost_terms)));
                break;
            case R.id.nav_menu_logout:
                logout();
                intent = new Intent(getBaseContext(), MainActivity.class);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        if (finish)
            finish();
        startActivity(intent);

        /*if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }*/

        return false;
    }

    protected void logout() {
        getApplicationContext().deleteDatabase(getString(R.string.database_name));

        SharedPreferencesManager manager = new SharedPreferencesManager(BaseActivity.this, "currentUser");
        manager.clear();
        id = null;
        user = null;
        isSubUser = false;
        if (userId != null) {
            userId = null;
            manager = new SharedPreferencesManager(BaseActivity.this, "currentSupervisor");
            manager.clear();
        }
    }
}

