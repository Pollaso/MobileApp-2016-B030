package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.model.pojo.Vehicle;
import ipn.mobileapp.presenter.dialogs.VehicleDialog;

public class VehicleAdapter extends ArrayAdapter<Vehicle> {
    private Context context;
    private int resource;
    private DialogInterface.OnDismissListener dismissListener;

    public VehicleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects, DialogInterface.OnDismissListener dismissListener) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.dismissListener = dismissListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Vehicle vehicle = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        ImageButton imgBtnEditContact = (ImageButton) convertView.findViewById(R.id.img_btn_edit_contact);
        imgBtnEditContact.setOnClickListener(new VehicleDialog(context, vehicle, dismissListener, Crud.UPDATE));

        ImageButton imgBtnDeleteContact = (ImageButton) convertView.findViewById(R.id.img_btn_delete_contact);
        imgBtnDeleteContact.setOnClickListener(new VehicleDialog(context, vehicle, dismissListener, Crud.DELETE));

        TextView tvRegistrationPlates = (TextView) convertView.findViewById(R.id.tv_registration_plates);
        tvRegistrationPlates.setText(vehicle.getCarPlates());

        TextView tvSerialKey = (TextView) convertView.findViewById(R.id.tv_serial_key);
        tvSerialKey.setText(vehicle.getDevice().getSerialKey());

        TextView tvOwnerName = (TextView) convertView.findViewById(R.id.tv_owner);
        tvOwnerName.setText(vehicle.getOwner());

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_user);
        tvUserName.setText(vehicle.getUser());

        return convertView;
    }
}
