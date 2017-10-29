package ipn.mobileapp.model.service.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.UUID;

import ipn.mobileapp.model.pojo.User;

public class BluetoothService extends Service {
    private BluetoothAdapter bluetoothAdapter = null;
    private String bluetoothDeviceAddress;
    private User user;

    private ConnectionThread connectionThread;
    private BluetoothSocket bluetoothSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                bluetoothDeviceAddress = extras.getString("address");
                user = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create().fromJson(extras.getString("user"), User.class);
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (!setupBluetooth()) {
                        Toast.makeText(getApplicationContext(), "Por favor, verifique que se encuentre encendido el dispositivo y configúrelo.", Toast.LENGTH_LONG).show();
                        BluetoothService.this.stopSelf();
                    }
                }
            });
        }
    }

    private boolean setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Este dispositivo no cuenta con Bluetooth", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!bluetoothAdapter.isEnabled())
            return false;

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            bluetoothSocket.connect();
            connectionThread = new ConnectionThread(bluetoothSocket, getApplicationContext(), user);
            connectionThread.start();
            connectionThread.write("x");
        } catch (IOException ce) {
            Toast.makeText(getBaseContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
            try {
                bluetoothSocket.close();
                return false;
            } catch (IOException te) {
                return false;
            }
        }

        Toast.makeText(getBaseContext(), "Conexión exitosa con el dispositivo", Toast.LENGTH_LONG).show();
        return true;
    }
}
