package ipn.mobileapp.presenter.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.NetworkHelper;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePasswordDialog implements MenuItem.OnMenuItemClickListener {
    private static int CHANGE_PASSWORD;

    private int minPasswordLength;

    private Context context;
    private AlertDialog dialog;

    private EditText etPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSaveChanges;
    private Button btnCancel;

    private User user;

    public ChangePasswordDialog(Context context, User user) {
        this.user = user;
        this.context = context;
        CHANGE_PASSWORD = context.getResources().getInteger(R.integer.update_change_password);
        minPasswordLength = context.getResources().getInteger(R.integer.password_min_length);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        createDialog();
        getComponents();
        setComponentAttributes();

        return true;
    }

    private void createDialog() {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_change_password)
                .setTitle(context.getString(R.string.title_dialog_change_password))
                .create();
        dialog.show();
    }

    private void getComponents() {
        btnSaveChanges = (Button) dialog.findViewById(R.id.btn_forgot_password);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        etPassword = (EditText) dialog.findViewById(R.id.et_password);
        etNewPassword = (EditText) dialog.findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText) dialog.findViewById(R.id.et_confirm_password);
    }

    private void setComponentAttributes() {
        btnSaveChanges.setOnClickListener(forgotPassword);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etPassword, etNewPassword, etConfirmPassword};
        etPassword.addTextChangedListener(new TextValidator(etPassword) {
            @Override
            public void validate(TextView textView, String text) {
                if (text != null && !text.equals("") && text.length() >= minPasswordLength)
                    user.setPassword(text);
                btnSaveChanges.setEnabled(validator.validateFields(fields));
            }
        });
        etNewPassword.addTextChangedListener(new TextValidator(etNewPassword) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidPassword(text))
                    etNewPassword.setError(context.getString(R.string.warning_password));
                else {
                    final String passwordStr = etConfirmPassword.getText().toString();
                    if (!passwordStr.equals(text))
                        etConfirmPassword.setError(context.getString(R.string.warning_password_matches));
                    else
                        etConfirmPassword.setError(null);

                    btnSaveChanges.setEnabled(validator.validateFields(fields));
                }
            }
        });
        etConfirmPassword.addTextChangedListener(new TextValidator(etConfirmPassword) {
            @Override
            public void validate(TextView textView, String text) {
                final String passwordStr = etNewPassword.getText().toString();
                if (!passwordStr.equals(text))
                    etConfirmPassword.setError(context.getString(R.string.warning_password_matches));
                else
                    btnSaveChanges.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private void processResults(final String response) {
        dialog.dismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String message = context.getString(R.string.error_server);
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data"))
                        message = context.getString(R.string.msj_change_password);
                    else if (json.has("warnings") && json.getAsJsonObject("warnings").has("noneUpdated"))
                        message = context.getString(R.string.warning_wrong_password);
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button.OnClickListener forgotPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.hide();

            if (!new NetworkHelper(context).hasNetworkConnection()) {
                Toast.makeText(context, context.getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            user.setPassword(etPassword.getText().toString());
            Map<String, String> params = new HashMap<>();
            params.put("user", user.toString());
            params.put("operation", String.valueOf(CHANGE_PASSWORD));
            params.put("newPassword", etConfirmPassword.getText().toString());

            OkHttpServletRequest request = new OkHttpServletRequest(context);
            Request builtRequest = request.buildRequest(Servlets.USER, RequestType.PUT, params);
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    processResults(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    processResults(response.body().string());
                }
            });
        }
    };
}
