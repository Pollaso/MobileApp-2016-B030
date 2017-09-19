package ipn.mobileapp.presenter.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ipn.mobileapp.debug.DebugMode;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JSONUtils;
import ipn.mobileapp.model.pojo.Device;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.pojo.Vehicle;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton ibtnBirthdate;
    private Button btnLogin;
    private Button btnRegister;
    private TextView birthdate;
    private TextView terms;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText name;
    private EditText paternalSurname;
    private EditText maternalSurname;
    private EditText phoneNumber;
    private EditText carPlates;
    private EditText serialKey;

    private User user;
    private Vehicle vehicle;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = new User();
        vehicle = new Vehicle();
        device = new Device();

        setButtons();
        setTerms();
        getComponents();
    }

    public void setTerms() {
        String htmlUrl = "<a 'href='" + getResources().getString(DebugMode.ON ? R.string.localhost_terms : R.string.server_terms) + "'>Términos y Condiciones</a>";
        terms = (TextView) findViewById(R.id.txtv_terms);
        terms.setClickable(true);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        terms.setText(Html.fromHtml(htmlUrl));
    }

    private void getComponents() {
        birthdate = (TextView) findViewById(R.id.txtv_birthdate);

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        name = (EditText) findViewById(R.id.et_name);
        paternalSurname = (EditText) findViewById(R.id.et_paternal_surname);
        maternalSurname = (EditText) findViewById(R.id.et_maternal_surname);
        phoneNumber = (EditText) findViewById(R.id.et_phone_number);

        carPlates = (EditText) findViewById(R.id.et_car_plates);
        serialKey = (EditText) findViewById(R.id.et_serial_key);
    }

    private void setButtons() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        ibtnBirthdate = (ImageButton) findViewById(R.id.ib_birthdate);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(register);

        ibtnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
    }

    private void createUser() {
        user.setEmail(email.getText().toString());
        if (!password.getText().toString().equals(confirmPassword.getText().toString()))
            return;
        user.setPassword(password.getText().toString());
        user.setName(name.getText().toString());
        user.setPaternalSurname(paternalSurname.getText().toString());
        user.setMaternalSurname(maternalSurname.getText().toString());
        user.setPhoneNumber(phoneNumber.getText().toString());
        user.setRole(User.USER_ROLE);

        device.setSerialKey(serialKey.getText().toString());
        vehicle.setCarPlates(carPlates.getText().toString());
        vehicle.setDevice(device);
        Collection<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);
        user.setVehicles(vehicles);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            int year = today.year;
            int month = today.month;
            int day = today.weekDay;

            return new DatePickerDialog(this, dateSetListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            Date selected = new Date(calendar.getTimeInMillis());
            user.setBirthdate(selected);

            //Formatter formatter = s new Formatter(Resources.getSystem().getConfiguration().locale);
            Formatter formatter = new Formatter(new Locale("es", "ES"));
            formatter.format("%tB %td %tY", calendar, calendar, calendar);
            birthdate.setText(formatter.toString());
        }
    };

    private Button.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createUser();
            Map<String, String> params = new HashMap<>();
            params.put("user", user.toString());

            ServletRequest request = new ServletRequest(getBaseContext());
            Request builtRequest = request.buildRequest(Servlets.REGISTER, RequestType.POST, params);
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    displayResults(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    displayResults(response.body().string());
                }
            });
            ///OkHttpSyncHandler handle = new OkHttpSyncHandler(client);
            //handle.execute(builtRequest);
        }
    };


    public void displayResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        User user = new Gson().fromJson(json.getAsJsonObject("data"), User.class);
                        //Write to UserDAO
                        user.setPassword(null);
                        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                        editor.clear();
                        editor.putString("user", json.getAsJsonObject("data").getAsString());
                        editor.commit();
                        Toast.makeText(RegisterActivity.this, getString(R.string.msj_successful_registration), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), ConfirmPhoneNumber.class);
                        startActivity(intent);
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        String message = warnings.has("user") ? getString(R.string.warning_register_user) : getString(R.string.warning_register_vehicle);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class OkHttpSyncHandler extends AsyncTask<Request, Void, String> {

        OkHttpClient client;

        public OkHttpSyncHandler(OkHttpClient client) {
            this.client = client;
        }

        @Override
        protected String doInBackground(Request... params) {
            try {
                Response response = client.newCall(params[0]).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            displayResults(s);
        }
    }
}


