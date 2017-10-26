package ipn.mobileapp.presenter.dialogs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.presenter.adapter.PairedDeviceAdapter;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;

public class PairedDevicesDialog{
    private Context context;
    private AlertDialog dialog;

    private ListView lvPairedDevices;
    private Button btnScan;
    private Button btnSettings;
    private LinearLayout llInstructions;

    public PairedDevicesDialog(Context context, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        createDialog(dismissListener);
        getComponents();
        setComponentAttributes();
    }

    private void createDialog(DialogInterface.OnDismissListener dismissListener) {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_paired_devices)
                .setTitle(context.getString(R.string.title_dialog_paring_intructions))
                .setOnDismissListener(dismissListener)
                .create();
        dialog.show();
    }

    private void getComponents() {
        lvPairedDevices = (ListView) dialog.findViewById(R.id.lv_paired_devices);
        llInstructions = (LinearLayout) dialog.findViewById(R.id.ll_instructions);
        btnSettings = (Button) dialog.findViewById(R.id.btn_settings);
        btnScan = (Button) dialog.findViewById(R.id.btn_scan);
    }

    private void setComponentAttributes() {
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                context.startActivity(intentOpenBluetoothSettings);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llInstructions.setVisibility(View.GONE);
                scanPairedDevices();
            }
        });
    }

    private void scanPairedDevices()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices == null || pairedDevices.size() == 0) {
            return;
        } else {
            ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
            bluetoothDevices.addAll(pairedDevices);
            PairedDeviceAdapter adapter =  new PairedDeviceAdapter(context, R.layout.listview_paired_device_item, bluetoothDevices, dialog);
            lvPairedDevices.setAdapter(adapter);
        }

        lvPairedDevices.setVisibility(View.VISIBLE);
    }
}
