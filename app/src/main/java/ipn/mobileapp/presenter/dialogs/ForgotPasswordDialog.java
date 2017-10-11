package ipn.mobileapp.presenter.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
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

public class ForgotPasswordDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog dialog;

    private EditText etEmail;
    private Button btnForgotPassword;
    private Button btnCancel;

    public ForgotPasswordDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
    }

    private void createDialog() {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_forgot_password)
                .setTitle(context.getString(R.string.title_dialog_forgot_password))
                .create();
        dialog.show();
    }

    private void getComponents() {
        btnForgotPassword = (Button) dialog.findViewById(R.id.btn_forgot_password);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        etEmail = (EditText) dialog.findViewById(R.id.et_email);
    }

    private void setComponentAttributes() {
        btnForgotPassword.setOnClickListener(forgotPassword);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etEmail};
        etEmail.addTextChangedListener(new TextValidator(etEmail) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidEmail(text))
                    etEmail.setError(context.getString(R.string.warning_email));
                else
                    btnForgotPassword.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private void processResults(final String response) {
        dialog.dismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.get("data").getAsBoolean())
                        Toast.makeText(context, context.getString(R.string.msj_forgot_password), Toast.LENGTH_SHORT).show();
                    else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        String message = warnings.has("user") ? warnings.get("user").getAsString() : warnings.get("instructions").getAsString();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
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

            User user = new User();
            user.setEmail(etEmail.getText().toString());
            Map<String, String> params = new HashMap<>();
            params.put("user", user.toString());

            OkHttpServletRequest request = new OkHttpServletRequest(context);
            Request builtRequest = request.buildRequest(Servlets.FORGOT_PASSWORD, RequestType.POST, params);
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
