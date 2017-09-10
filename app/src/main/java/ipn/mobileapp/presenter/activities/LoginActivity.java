package ipn.mobileapp.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.ServletRequest;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnRegister;
    private Button btnForgotPassword;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        setButtons();
    }

    private void setButtons() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        User user = new User();
                        user.setEmail(email.getText().toString());
                        user.setPassword(password.getText().toString());
                        Map<String, String> params = new HashMap<>();
                        params.put("user", user.toString());

                        ServletRequest request = new ServletRequest(getBaseContext());
                        Request builtRequest = null;
                        builtRequest = request.create(Servlets.LOGIN, RequestType.GET, params);
                        String response = null;
                        if (builtRequest != null)
                            response = request.execute(builtRequest);
                        if (response != null) {
                            user = new Gson().fromJson(response, User.class);
                            System.out.println(user.toString());
                        }
                    }
                }).start();
                //Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                //startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
