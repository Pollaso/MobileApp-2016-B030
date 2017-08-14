package ipn.mobileapp.presenter.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ipn.mobileapp.R;

public class VehicleDialog implements View.OnClickListener {
    private Context context;

    public VehicleDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.dialog_vehicle);
        Button addVehicle = (Button) openDialog.findViewById(R.id.btn_add_vehicle);
        openDialog.setTitle(context.getString(R.string.title_dialog_add_vehicle));
        openDialog.show();

        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, R.string.msj_contact_added, Toast.LENGTH_LONG).show();
                openDialog.dismiss();
            }
        });
    }
}
