package ipn.mobileapp.presenter.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
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

import ipn.mobileapp.R;
import ipn.mobileapp.debug.DebugMode;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.service.bluetooth.BluetoothService;
import ipn.mobileapp.model.service.dao.user.IUserSchema;
import ipn.mobileapp.presenter.dialogs.PairedDevicesDialog;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private NavigationView navigationView;
    private LinearLayout navHeader;
    private Menu navMenu;
    private AlertDialog progressDialog;

    private SharedPreferencesManager manager;

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;
    private static int bluetoothCounter = 0;

    protected static User user;

    protected static String id;
    protected static String userId;
    protected static String bluetoothDeviceAddress;
    protected static boolean isSubUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        if (id == null)
            id = (String) manager.getValue("id", String.class);
        if (user == null)
            getUser();

        if (!user.isEnabled()) {
            user = null;
            Intent intent = new Intent(getBaseContext(), ConfirmPhoneActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (isSubUser && userId == null) {
            manager = new SharedPreferencesManager(BaseActivity.this, "currentSupervisor");
            userId = (String) manager.getValue("userId", String.class);
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
            return;
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            if (bluetoothDeviceAddress == null)
                setBluetoothDeviceAddress();
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
        if (user != null) {
            TextView tvHeaderName = (TextView) header.findViewById(R.id.header_name);
            TextView tvHeaderEmail = (TextView) header.findViewById(R.id.header_email);
            tvHeaderName.setText(user.getName());
            tvHeaderEmail.setText(user.getEmail());
        }

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
                bluetoothCounter = 0;
                finish();
                System.exit(0);
            } else {
                Toast.makeText(BaseActivity.this, "Presione el botón de regresar de nuevo para cerrar la aplicación", Toast.LENGTH_SHORT).show();
            }

            backPressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK)
            setBluetoothDeviceAddress();
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
            case R.id.nav_menu_configure:
                finish = false;
                new PairedDevicesDialog(BaseActivity.this, dismissPairedDevices);
                break;
            /*case R.id.nav_menu_about_us:
                finish = false;
                intent = new Intent(getBaseContext(), HomeActivity.class);
                break;*/
            case R.id.nav_menu_privacy:
                finish = false;
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(DebugMode.ON ? R.string.localhost_terms : R.string.server_terms)));
                break;
            case R.id.nav_menu_logout:
                logout();
                intent = new Intent(getBaseContext(), MainActivity.class);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        if (finish)
            finish();
        if (intent != null)
            startActivity(intent);

        /*if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }*/

        return true;
    }

    protected void logout() {
        Intent intent = new Intent(BaseActivity.this, BluetoothService.class);
        stopService(intent);

        getApplicationContext().deleteDatabase(getString(R.string.database_name));

        SharedPreferencesManager manager = new SharedPreferencesManager(BaseActivity.this, "currentUser");
        manager.clear();
        id = null;
        user = null;
        bluetoothCounter = 0;
        isSubUser = false;
        if (userId != null) {
            userId = null;
            manager = new SharedPreferencesManager(BaseActivity.this, "currentSupervisor");
            manager.clear();
        }
    }

    private DialogInterface.OnDismissListener dismissPairedDevices = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            MenuItem menuItem = navMenu.getItem(6);
            menuItem.setChecked(false);
            setBluetoothDeviceAddress();
        }
    };

    public void setBluetoothDeviceAddress() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                manager = new SharedPreferencesManager(BaseActivity.this, getString(R.string.bluetooth_device_filename));
                String address = (String) manager.getValue("address", String.class);

                if(bluetoothCounter > 0)
                    return;

                if (address == null) {
                    bluetoothCounter++;
                    new PairedDevicesDialog(BaseActivity.this, dismissPairedDevices);
                    return;
                }

                bluetoothDeviceAddress = address;
                connectToDevice();
            }
        });
    }

    protected void showProgressDialog() {
        progressDialog = new AlertDialog.Builder(BaseActivity.this)
                .setView(R.layout.dialog_progress)
                .setTitle("Espere por favor")
                .setCancelable(true)
                .create();
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void connectToDevice() {
        Intent intent = new Intent(BaseActivity.this, BluetoothService.class);
        stopService(intent);
        intent.putExtra("address", bluetoothDeviceAddress);
        intent.putExtra("user", user.toString());
        startService(intent);
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(BaseActivity.this);

        alertDialog.setCancelable(false);

        alertDialog.setTitle(getString(R.string.title_dialog_gps_settings));

        alertDialog.setMessage(getString(R.string.tv_gps_disabled));

        alertDialog.setPositiveButton(getString(R.string.btn_activate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }
}

