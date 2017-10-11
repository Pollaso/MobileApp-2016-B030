package ipn.mobileapp.presenter.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import ipn.mobileapp.debug.DebugMode;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.model.pojo.Device;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.pojo.Vehicle;
import ipn.mobileapp.R;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etName;
    private EditText etPaternalSurname;
    private EditText etMaternalSurname;
    private Spinner spnrCountryCode;
    private TextView tvCountryCodeNumber;
    private EditText etPhoneNumber;
    private ImageButton imgBtnBirthdate;
    private TextView tvBirthdate;
    private EditText etCarPlates;
    private EditText etSerialKey;
    private TextView tvTerms;
    private CheckBox cbTerms;

    private Button btnLogin;
    private Button btnRegister;

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

        getComponents();
        setComponentAttributes();
        setTerms();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            today.year -= getResources().getInteger(R.integer.legal_permit_age);

            DatePickerDialog datePicker = new DatePickerDialog(this, dateSetListener, today.year, today.month, today.monthDay);
            datePicker.getDatePicker().setMaxDate(today.toMillis(false));
            return datePicker;
        }
        return null;
    }

    private void setTerms() {
        String htmlUrl = "<a 'href='" + getResources().getString(DebugMode.ON ? R.string.localhost_terms : R.string.server_terms) + "'>Términos y Condiciones</a>";

        tvTerms.setClickable(true);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setText(Html.fromHtml(htmlUrl));
    }

    private void getComponents() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        imgBtnBirthdate = (ImageButton) findViewById(R.id.ib_birthdate);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        etName = (EditText) findViewById(R.id.et_name);
        etPaternalSurname = (EditText) findViewById(R.id.et_paternal_surname);
        etMaternalSurname = (EditText) findViewById(R.id.et_maternal_surname);
        spnrCountryCode = (Spinner) findViewById(R.id.s_country_code);
        tvCountryCodeNumber = (TextView) findViewById(R.id.txtv_country_code);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        tvBirthdate = (TextView) findViewById(R.id.txtv_birthdate);

        etCarPlates = (EditText) findViewById(R.id.et_car_plates);
        etSerialKey = (EditText) findViewById(R.id.et_serial_key);
        tvTerms = (TextView) findViewById(R.id.txtv_terms);
        cbTerms = (CheckBox) findViewById(R.id.cb_terms);
    }

    private void setComponentAttributes() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(register);

        imgBtnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        final Validator validator = new Validator(this);
        final TextView[] fields = new TextView[]{etEmail, etPassword, etConfirmPassword, etName, etPaternalSurname, etMaternalSurname, tvCountryCodeNumber, etPhoneNumber, tvBirthdate, etCarPlates, etSerialKey};

        etEmail.addTextChangedListener(new TextValidator(etEmail) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidEmail(text))
                    etEmail.setError(getString(R.string.warning_email));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etPassword.addTextChangedListener(new TextValidator(etPassword) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidPassword(text))
                    etPassword.setError(getString(R.string.warning_password));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etConfirmPassword.addTextChangedListener(new TextValidator(etConfirmPassword) {
            @Override
            public void validate(TextView textView, String text) {
                final String passwordStr = etPassword.getText().toString();
                if (!passwordStr.equals(text))
                    etConfirmPassword.setError(getString(R.string.warning_password_matches));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etName.addTextChangedListener(new TextValidator(etName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidName(text))
                    etName.setError(getString(R.string.warning_name));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etPaternalSurname.addTextChangedListener(new TextValidator(etPaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etPaternalSurname.setError(getString(R.string.warning_surname));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etMaternalSurname.addTextChangedListener(new TextValidator(etMaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etMaternalSurname.setError(getString(R.string.warning_surname));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        spnrCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvCountryCodeNumber.setText(getResources().getStringArray(R.array.s_country_codes)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        etPhoneNumber.addTextChangedListener(new TextValidator(etPhoneNumber) {
            @Override
            public void validate(TextView textView, String text) {
                final String phone = tvCountryCodeNumber.getText().toString() + text;
                if (!validator.isValidPhone(phone))
                    etPhoneNumber.setError(getString(R.string.warning_phone_number));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etCarPlates.addTextChangedListener(new TextValidator(etCarPlates) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidCarPlates(text))
                    etCarPlates.setError(getString(R.string.warning_car_plates));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
        etSerialKey.addTextChangedListener(new TextValidator(etSerialKey) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidUUID(text))
                    etSerialKey.setError(getString(R.string.warning_serial_key));
                else
                    btnRegister.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private void createUser() {
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setName(etName.getText().toString());
        user.setPaternalSurname(etPaternalSurname.getText().toString());
        user.setMaternalSurname(etMaternalSurname.getText().toString());
        user.setPhoneNumber(tvCountryCodeNumber.getText().toString() + etPhoneNumber.getText().toString());
        user.setRole(User.USER_ROLE);

        device.setSerialKey(etSerialKey.getText().toString());
        vehicle.setLicensePlate(etCarPlates.getText().toString());
        vehicle.setDevice(device);
        Collection<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);
        user.setVehicles(vehicles);
    }

    private void displayResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        User user = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setDateFormat("yyyy-MM-dd").create().fromJson(json.get("data").getAsString(), User.class);

                        /* Using OrmLite */
                        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                        try {
                            final Dao<User, String> userDao = databaseHelper.getUserDao();
                            userDao.create(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            databaseHelper.close();
                        }

                        SharedPreferencesManager manager = new SharedPreferencesManager(RegisterActivity.this, "currentUser");
                        manager.putValue("id", user.getId(), true);

                        Toast.makeText(RegisterActivity.this, getString(R.string.msj_successful_registration), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), ConfirmPhoneActivity.class);
                        finish();
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
            tvBirthdate.setText(formatter.toString());
        }
    };

    private Button.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!cbTerms.isChecked()) {
                tvTerms.requestFocus();
                tvTerms.setError(getString(R.string.warning_terms));
            }

            createUser();
            Map<String, String> params = new ArrayMap<>();
            params.put("user", user.toString());

            OkHttpServletRequest request = new OkHttpServletRequest(getBaseContext());
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
        }
    };



}


