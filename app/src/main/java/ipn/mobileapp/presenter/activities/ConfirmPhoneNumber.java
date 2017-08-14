package ipn.mobileapp.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ipn.mobileapp.R;

public class ConfirmPhoneNumber extends AppCompatActivity {
    private Button btnConfirmCode;
    private Button btnResendCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone_number);
        setUpButtons();
    }

    private void setUpButtons() {
        btnConfirmCode = (Button) findViewById(R.id.btn_confirm_code);
        btnResendCode = (Button) findViewById(R.id.btn_resend_code);

        btnConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msj_phone_confirmed), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msj_resend_code), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
//                startActivity(intent);
            }
        });
    }
}
