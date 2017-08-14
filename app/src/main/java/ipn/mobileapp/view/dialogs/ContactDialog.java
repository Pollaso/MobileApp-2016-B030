package ipn.mobileapp.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ipn.mobileapp.R;

public class ContactDialog implements View.OnClickListener {
    private Context context;
    private boolean editMode;

    public ContactDialog(Context context, boolean editMode) {
        this.context = context;
        this.editMode = editMode;
    }

    @Override
    public void onClick(View v) {
        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.dialog_contacts);
        Button saveContact = (Button) openDialog.findViewById(R.id.btn_save_edit);
        if (editMode) {
            openDialog.setTitle(context.getString(R.string.title_dialog_edit_contact));
            saveContact.setText(context.getString(R.string.btn_save_changes));
        } else {
            openDialog.setTitle(context.getString(R.string.title_dialog_add_contact));
            saveContact.setText(context.getString(R.string.btn_save_contact));
        }
        openDialog.show();

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (editMode)
                    Toast.makeText(context, R.string.msj_contact_saved, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, R.string.msj_contact_added, Toast.LENGTH_LONG).show();
                openDialog.dismiss();
            }
        });
    }
}
