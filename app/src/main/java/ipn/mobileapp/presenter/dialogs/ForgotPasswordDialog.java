package ipn.mobileapp.presenter.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JSONUtils;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.ServletRequest;
import okhttp3.Request;

public class ForgotPasswordDialog implements View.OnClickListener {
    private Context context;
    private EditText etEmail;
    private JsonObject json;

    public ForgotPasswordDialog(Context context) {
        this.context = context;
    }

    private void createDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_forgot_password)
                .setTitle(context.getString(R.string.title_dialog_forgot_password))
                .create();
        dialog.show();

        etEmail = (EditText) dialog.findViewById(R.id.et_email);
        final Button btnForgotPassword = (Button) dialog.findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                String email = etEmail.getText().toString();
                if (email.equalsIgnoreCase(""))
                    return;
                user.setEmail(email);
                Map<String, String> params = new HashMap<>();
                params.put("user", user.toString());

                final ServletRequest request = new ServletRequest(context);
                final Request builtRequest = request.create(Servlets.FORGOT_PASSWORD, RequestType.POST, params);
                try {
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            String response = null;
                            if (builtRequest != null)
                                response = request.execute(builtRequest);
                            if (JSONUtils.isValidJSON(response))
                                json = (JsonObject) new JsonParser().parse(response);
                        }
                    });
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                displayResults();
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void displayResults() {
        if (json.get("data").getAsBoolean())
            Toast.makeText(context, context.getString(R.string.msj_forgot_password), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        createDialog();
    }
}
