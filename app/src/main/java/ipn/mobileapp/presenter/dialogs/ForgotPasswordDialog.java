package ipn.mobileapp.presenter.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ipn.mobileapp.R;

public class ForgotPasswordDialog implements View.OnClickListener {
    private Context context;

    public ForgotPasswordDialog(Context context) {
        this.context = context;
    }

    private void createDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_forgot_password)
                .setTitle(context.getString(R.string.title_dialog_forgot_password))
                .create();
        dialog.show();

        Button btnForgotPassword = (Button) dialog.findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(forgotPassword);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        createDialog();
    }

    private Button.OnClickListener forgotPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, R.string.msj_forgot_password, Toast.LENGTH_LONG).show();
        }
    };
}
