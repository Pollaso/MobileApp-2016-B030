package ipn.mobileapp.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JsonUtils;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.service.dao.user.IUserSchema;
import ipn.mobileapp.presenter.validation.TextValidator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfirmPhoneActivity extends AppCompatActivity {
    private EditText etConfirmationCode;

    private Button btnConfirmCode;
    private Button btnResendCode;

    private String confirmationId;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone);

        SharedPreferencesManager manager = new SharedPreferencesManager(ConfirmPhoneActivity.this, getString(R.string.current_user_filename));
        _id = (String) manager.getValue("_id", String.class);

        requestConfirmationCode(false);

        getComponents();
        setComponentAttributes();
    }

    public void getComponents() {
        etConfirmationCode = (EditText) findViewById(R.id.et_confirmation_code);

        btnConfirmCode = (Button) findViewById(R.id.btn_confirm_code);
        btnResendCode = (Button) findViewById(R.id.btn_resend_code);
    }

    public void setComponentAttributes() {
        etConfirmationCode.addTextChangedListener(new TextValidator(etConfirmationCode) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() != 6)
                    etConfirmationCode.setError(getString(R.string.warning_confirmation_code));
                else
                    btnConfirmCode.setEnabled(true);
            }
        });

        btnConfirmCode.setOnClickListener(confirmCode);

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestConfirmationCode(true);
            }
        });
    }

    private void requestCodeResults(final String response, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        confirmationId = json.get("data").getAsString();
                        Toast.makeText(ConfirmPhoneActivity.this, message, Toast.LENGTH_LONG).show();
                    } else if (json.has("errors") && json.getAsJsonObject("errors").get("smsError").getAsBoolean()) {
                        Toast.makeText(ConfirmPhoneActivity.this, getString(R.string.error_confirmation_sms), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(ConfirmPhoneActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestConfirmationCode(final boolean resend) {
        Map<String, String> params = new HashMap<>();
        params.put("_id", _id);

        ServletRequest request = new ServletRequest(ConfirmPhoneActivity.this);
        Request builtRequest = request.buildRequest(Servlets.VERIFY_PHONE, RequestType.GET, params);
        OkHttpClient client = request.buildClient();
        client.newCall(builtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requestCodeResults(null, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                requestCodeResults(response.body().string(), getString(resend ? R.string.msj_resend_code : R.string.msj_confirmation_sms_sent));
            }
        });
    }

    private void confirmCodeResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                        try {
                            final Dao<User, String> userDao = databaseHelper.getUserDao();
                            UpdateBuilder<User, String> updateBuilder = userDao.updateBuilder();
                            updateBuilder.updateColumnValue(IUserSchema.COLUMN_ENABLED, json.get("data").getAsBoolean());
                            updateBuilder.where().eq(IUserSchema.COLUMN_ID, _id);
                            updateBuilder.update();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            databaseHelper.close();
                        }

                        Toast.makeText(ConfirmPhoneActivity.this, getString(R.string.msj_phone_confirmed), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                        finish();
                        startActivity(intent);
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        String message = null;
                        if (warnings.has("codeNotFound"))
                            message = getString(R.string.warning_confirmation_code_not_found);
                        else if (warnings.has("usedCode"))
                            message = getString(R.string.warning_confirmation_code_used);
                        else if (warnings.has("notCurrentCode"))
                            message = getString(R.string.warning_confirmation_code_not_current);
                        Toast.makeText(ConfirmPhoneActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ConfirmPhoneActivity.this, getString(R.string.error_phone_confirmation), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ConfirmPhoneActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button.OnClickListener confirmCode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject json = new JsonObject();
            json.addProperty("confirmationId", confirmationId);
            json.addProperty("code", etConfirmationCode.getText().toString());
            json.addProperty("userId", _id);

            Map<String, String> params = new HashMap<>();
            params.put("smsCode", json.toString());

            ServletRequest request = new ServletRequest(ConfirmPhoneActivity.this);
            Request builtRequest = request.buildRequest(Servlets.VERIFY_PHONE, RequestType.POST, params);
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    confirmCodeResults(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    confirmCodeResults(response.body().string());
                }
            });
        }
    };

}
