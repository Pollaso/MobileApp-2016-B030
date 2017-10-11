package ipn.mobileapp.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
//import ipn.mobileapp.model.service.dao.Database;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.dialogs.ForgotPasswordDialog;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnRegister;
    private Button btnForgotPassword;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        getComponents();
        setComponentAttributes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
    }

    private void getComponents() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);
    }

    private void setComponentAttributes() {
        btnLogin.setOnClickListener(login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnForgotPassword.setOnClickListener(new ForgotPasswordDialog(this));

        final Validator validator = new Validator(this);
        final TextView[] fields = new TextView[]{etEmail, etPassword};
        etEmail.addTextChangedListener(new TextValidator(etEmail) {
            @Override
            public void validate(TextView textView, String text) {
                btnLogin.setEnabled(validator.validateFields(fields));
            }
        });
        etPassword.addTextChangedListener(new TextValidator(etPassword) {
            @Override
            public void validate(TextView textView, String text) {
                btnLogin.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private void processResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        User user = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setDateFormat("yyyy-MM-dd").create().fromJson(json.get("data").getAsString(), User.class);

                        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                        try {
                            final Dao<User, String> userDao = databaseHelper.getUserDao();
                            userDao.create(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            databaseHelper.close();
                        }

                        SharedPreferencesManager manager = new SharedPreferencesManager(LoginActivity.this, "currentUser");
                        manager.putValue("id", user.getId(), true);
                        if (user.getRole().equals(User.SUBUSER_ROLE))
                            manager.putValue("userId", user.getUserId(), true);

                        Class redirection = user.isEnabled() ? HomeActivity.class : ConfirmPhoneActivity.class;
                        Intent intent = new Intent(getBaseContext(), redirection);
                        finish();
                        startActivity(intent);
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        String message = warnings.has("user") ? getString(R.string.warning_login_user) : getString(R.string.warning_wrong_password);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(LoginActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User user = new User();
            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());
            Map<String, String> params = new ArrayMap<>();
            params.put("user", user.toString());
            OkHttpServletRequest request = new OkHttpServletRequest(getBaseContext());
            Request builtRequest = request.buildRequest(Servlets.LOGIN, RequestType.GET, params);
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
