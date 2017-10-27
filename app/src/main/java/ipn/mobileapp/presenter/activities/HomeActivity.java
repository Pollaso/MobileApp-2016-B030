package ipn.mobileapp.presenter.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NoRouteToHostException;
import java.util.UUID;

import ipn.mobileapp.R;
import ipn.mobileapp.model.service.GpsService;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class HomeActivity extends BaseActivity {
    static {
        System.loadLibrary("native-lib");
    }

    private ConnectedThread connectedThread;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawer.addView(contentView, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Button button = new Button(this);
        button.setOnClickListener(new PairedDevicesDialog(HomeActivity.this));
        button.performClick();*/

        GpsService gpsService = new GpsService(this);
        gpsService.getLocation();
        gpsService.getAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupBluetooth();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    private void setupBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Este dispositivo no cuenta con Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if (bluetoothAdapter.isEnabled()) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
            }

            try {
                bluetoothSocket.connect();
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
                connectedThread.write("x");
            } catch (IOException ce) {
                try {
                    bluetoothSocket.close();
                } catch (IOException te) {
                    Toast.makeText(getBaseContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private Handler bluetoothHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {                                        //if message is what we want
                String readMessage = (String) msg.obj;                                                                //
            }
        }
    };

    private class ConnectedThread extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = socket.getInputStream();
                tempOutputStream = socket.getOutputStream();
            } catch (IOException e) {
            }

            this.inputStream = tempInputStream;
            this.outputStream = tempOutputStream;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String input = new String(buffer, 0, bytes);
                    bytes = inputStream.read(buffer);
                    input += new String(buffer, 0, bytes);

                    final String msj = input;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), msj, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(HomeActivity.this, AlertsActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] bytes = input.getBytes();
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
            }
        }
    }
}
