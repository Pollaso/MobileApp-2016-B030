package ipn.mobileapp.presenter.adapter;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ipn.mobileapp.R;
import ipn.mobileapp.model.service.SharedPreferencesManager;

public class PairedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
	private Context context;
	private int resource;
    private Dialog dialog;

	public PairedDeviceAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<BluetoothDevice> objects, Dialog dialog) {
		super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.dialog = dialog;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final BluetoothDevice bluetoothDevice = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
        }

        TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
        tvDeviceName.setText(bluetoothDevice.getName());
        TextView tvDeviceAddress = (TextView) convertView.findViewById(R.id.tv_device_address);
        tvDeviceAddress.setText(bluetoothDevice.getAddress());
        Button btnSelect = (Button) convertView.findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager manager = new SharedPreferencesManager(context, context.getString(R.string.bluetooth_device_filename));
                manager.putValue("address", bluetoothDevice.getAddress(), true);
                dialog.dismiss();
            }
        });

        return convertView;
	}
}