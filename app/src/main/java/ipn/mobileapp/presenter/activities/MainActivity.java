package ipn.mobileapp.presenter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.Button;

import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.service.SharedPreferencesManager;

public class MainActivity extends Activity {
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForCurrentSession();
        getComponents();
        setComponentAttributes();
    }

    private void checkForCurrentSession() {
        SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this, "currentUser");
        if (manager.getValue("_id", String.class) != null) {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void getComponents() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    private void setComponentAttributes() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }
}