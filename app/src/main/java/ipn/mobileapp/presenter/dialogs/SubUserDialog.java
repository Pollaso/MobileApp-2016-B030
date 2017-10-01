package ipn.mobileapp.presenter.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JsonUtils;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.presenter.activities.ConfirmPhoneActivity;
import ipn.mobileapp.presenter.activities.SubUsersActivity;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SubUserDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private DialogInterface.OnDismissListener dismissListener;

    private EditText etEmail;
    private EditText etName;
    private EditText etPaternalSurname;
    private EditText etMaternalSurname;
    private Spinner spnrCountryCode;
    private TextView tvCountryCodeNumber;
    private EditText etPhoneNumber;
    private TextView tvBirthdate;
    private ImageButton imgBtnBirthdate;

    private Button btnRegisterSubUser;
    private Button btnCancel;

    private User subUser;

    public SubUserDialog(Context context, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        this.dismissListener = dismissListener;
        subUser = new User();
    }

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
    }

    private void createDialog() {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_sub_users)
                .setTitle(context.getString(R.string.title_dialog_register_sub_user))
                .setCancelable(true)
                .setOnDismissListener(dismissListener)
                .create();
        dialog.show();
    }

    private void getComponents() {
        btnRegisterSubUser = (Button) dialog.findViewById(R.id.btn_register);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        etEmail = (EditText) dialog.findViewById(R.id.et_email);
        etName = (EditText) dialog.findViewById(R.id.et_name);
        etPaternalSurname = (EditText) dialog.findViewById(R.id.et_paternal_surname);
        etMaternalSurname = (EditText) dialog.findViewById(R.id.et_maternal_surname);
        spnrCountryCode = (Spinner) dialog.findViewById(R.id.s_country_code);
        tvCountryCodeNumber = (TextView) dialog.findViewById(R.id.txtv_country_code);
        etPhoneNumber = (EditText) dialog.findViewById(R.id.et_phone_number);
        tvBirthdate = (TextView) dialog.findViewById(R.id.txtv_birthdate);
        imgBtnBirthdate = (ImageButton) dialog.findViewById(R.id.ib_birthdate);
    }

    private void setComponentAttributes() {
        btnRegisterSubUser.setText(context.getString(R.string.btn_register_sub_user));
        btnRegisterSubUser.setOnClickListener(register);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etEmail, etName, etPaternalSurname, etMaternalSurname, tvCountryCodeNumber, etPhoneNumber, tvBirthdate};

        etEmail.addTextChangedListener(new TextValidator(etEmail) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidEmail(text))
                    etEmail.setError(context.getString(R.string.warning_email));
                else
                    btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        etName.addTextChangedListener(new TextValidator(etName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidName(text))
                    etName.setError(context.getString(R.string.warning_name));
                else
                    btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        etPaternalSurname.addTextChangedListener(new TextValidator(etPaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etPaternalSurname.setError(context.getString(R.string.warning_surname));
                else
                    btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        etMaternalSurname.addTextChangedListener(new TextValidator(etMaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etMaternalSurname.setError(context.getString(R.string.warning_surname));
                else
                    btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        spnrCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvCountryCodeNumber.setText(context.getResources().getStringArray(R.array.s_country_codes)[position]);
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
                    etPhoneNumber.setError(context.getString(R.string.warning_phone_number));
                else
                    btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        tvBirthdate.addTextChangedListener(new TextValidator(tvBirthdate) {
            @Override
            public void validate(TextView textView, String text) {
                btnRegisterSubUser.setEnabled(validator.validateFields(fields));
            }
        });
        imgBtnBirthdate.setOnClickListener(datePicker);
    }

    private void processResults(final String response) {
        dialog.dismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        User user = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setDateFormat("yyyy-MM-dd").create().fromJson(json.get("data").getAsString(), User.class);

                        /* Using OrmLite
                        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());
                        try {
                            final Dao<User, String> userDao = databaseHelper.getUserDao();
                            userDao.create(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            databaseHelper.close();
                        }*/

                        Toast.makeText(context, context.getString(R.string.msj_sub_user_registered), Toast.LENGTH_LONG).show();
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        if (warnings.has("user"))
                            Toast.makeText(context, context.getString(R.string.warning_register_user), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser() {
        subUser.setEmail(etEmail.getText().toString());
        subUser.setName(etName.getText().toString());
        subUser.setPaternalSurname(etPaternalSurname.getText().toString());
        subUser.setMaternalSurname(etMaternalSurname.getText().toString());
        subUser.setPhoneNumber(tvCountryCodeNumber.getText().toString() + etPhoneNumber.getText().toString());
        subUser.setRole(User.SUBUSER_ROLE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", MODE_PRIVATE);
        subUser.setUserId(sharedPreferences.getString("_id", null));
    }

    private ImageButton.OnClickListener datePicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            today.year -= context.getResources().getInteger(R.integer.legal_permit_age);

            DatePickerDialog datePicker = new DatePickerDialog(context, dateSetListener, today.year, today.month, today.monthDay);
            datePicker.getDatePicker().setMaxDate(today.toMillis(false));
            datePicker.show();
        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            Date selected = new Date(calendar.getTimeInMillis());
            subUser.setBirthdate(selected);

            //Formatter formatter = s new Formatter(Resources.getSystem().getConfiguration().locale);
            Formatter formatter = new Formatter(new Locale("es", "ES"));
            formatter.format("%tB %td %tY", calendar, calendar, calendar);
            tvBirthdate.setText(formatter.toString());
        }
    };

    private Button.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createUser();
            Map<String, String> params = new ArrayMap<>();
            params.put("user", subUser.toString());

            ServletRequest request = new ServletRequest(context);
            Request builtRequest = request.buildRequest(Servlets.REGISTER, RequestType.POST, params);
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
