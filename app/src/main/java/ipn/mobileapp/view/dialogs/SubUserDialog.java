package ipn.mobileapp.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import ipn.mobileapp.R;

public class SubUserDialog implements View.OnClickListener {
    private Context context;
    private FragmentManager fragmentManager;

    public SubUserDialog(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.dialog_sub_users);
        Button registerSubUser = (Button) openDialog.findViewById(R.id.btn_register);
        ImageButton ibtnBirthdate = (ImageButton) openDialog.findViewById(R.id.ib_birthdate);
        openDialog.setTitle(context.getString(R.string.title_dialog_register_sub_user));
        registerSubUser.setText(context.getString(R.string.btn_register_sub_user));
        openDialog.show();


        ibtnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(fragmentManager, "datePicker");
            }
        });

        registerSubUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, R.string.msj_successful_registration, Toast.LENGTH_LONG).show();
                openDialog.dismiss();
            }
        });
    }
}
