package ipn.mobileapp.presenter.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.NetworkHelper;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.dialogs.ChangePasswordDialog;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends BaseActivity {
    public static int DETAILS;
    public static int DISABLE;

    private EditText etEmail;
    private EditText etName;
    private EditText etPaternalSurname;
    private EditText etMaternalSurname;
    private TextView tvCountryCodeNumber;
    private TextView tvPhoneNumber;
    private ImageButton imgBtnBirthdate;
    private TextView tvBirthdate;

    private FloatingActionButton saveChanges;
    private FloatingActionButton modifyDocuments;
    private Context context = this;

    private User editedUser = new Gson().fromJson(user.toString(), User.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawer.addView(contentView, 0);

        final ScrollView profileScroll = (ScrollView) findViewById(R.id.sv_profile_scroll);
        profileScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (saveChanges.getVisibility() != View.GONE) {
                    setMainButtonsVisibility(View.GONE);
                } else {
                    if (profileScroll.getScrollY() != 0) {
                        setMainButtonsVisibility(View.GONE);
                    } else {
                        setMainButtonsVisibility(View.VISIBLE);
                    }
                }
            }
        });

        DETAILS = context.getResources().getInteger(R.integer.update_update_details);
        DISABLE = context.getResources().getInteger(R.integer.update_disable);

        getComponents();
        setComponentAttributes();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Time today = new Time(Time.getCurrentTimezone());
            today.set(editedUser.getBirthdate().getTime());

            DatePickerDialog datePicker = new DatePickerDialog(this, dateSetListener, today.year, today.month, today.monthDay);
            today.setToNow();
            today.year -= getResources().getInteger(R.integer.legal_permit_age);
            datePicker.getDatePicker().setMaxDate(today.toMillis(false));
            return datePicker;
        }
        return null;
    }

    private void getComponents() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etName = (EditText) findViewById(R.id.et_name);
        etPaternalSurname = (EditText) findViewById(R.id.et_paternal_surname);
        etMaternalSurname = (EditText) findViewById(R.id.et_maternal_surname);
        tvCountryCodeNumber = (TextView) findViewById(R.id.txtv_country_code);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        tvBirthdate = (TextView) findViewById(R.id.txtv_birthdate);

        imgBtnBirthdate = (ImageButton) findViewById(R.id.ib_birthdate);
        saveChanges = (FloatingActionButton) findViewById(R.id.fbtn_save_changes);
        modifyDocuments = (FloatingActionButton) findViewById(R.id.fbtn_modify_documents);
    }

    private void setComponentAttributes() {
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        saveChanges.setEnabled(false);

        imgBtnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        final Validator validator = new Validator(this);
        final TextView[] fields = new TextView[]{etEmail, etName, etPaternalSurname, etMaternalSurname, tvBirthdate};
        toggleEnabledAttribute(R.id.layout_edit_profile, false);
        etEmail.addTextChangedListener(new TextValidator(etEmail) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidEmail(text))
                    etEmail.setError(getString(R.string.warning_email));
                else {
                    editedUser.setEmail(text);
                    saveChanges.setEnabled(validator.validateFields(fields));
                }
            }
        });
        etName.addTextChangedListener(new TextValidator(etName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidName(text))
                    etName.setError(getString(R.string.warning_name));
                else {
                    editedUser.setName(text);
                    saveChanges.setEnabled(validator.validateFields(fields));
                }
            }
        });
        etPaternalSurname.addTextChangedListener(new TextValidator(etPaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etPaternalSurname.setError(getString(R.string.warning_surname));
                else {
                    editedUser.setPaternalSurname(text);
                    saveChanges.setEnabled(validator.validateFields(fields));
                }
            }
        });
        etMaternalSurname.addTextChangedListener(new TextValidator(etMaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etMaternalSurname.setError(getString(R.string.warning_surname));
                else {
                    editedUser.setMaternalSurname(text);
                    saveChanges.setEnabled(validator.validateFields(fields));
                }
            }
        });
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPhoneNumber.setError(getString(R.string.msj_change_phone_numer));
            }
        });

        setData();
        saveChanges.setEnabled(validator.validateFields(fields));
    }

    private void setData() {
        etEmail.setText(user.getEmail());
        etName.setText(user.getName());
        etPaternalSurname.setText(user.getPaternalSurname());
        etMaternalSurname.setText(user.getMaternalSurname());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(user.getBirthdate().getTime());
        Formatter formatter = new Formatter(new Locale("es", "ES"));
        formatter.format("%tB %td %tY", calendar, calendar, calendar);
        tvBirthdate.setText(formatter.toString());

        String phoneNumber = user.getPhoneNumber();
        String[] countryCodes = context.getResources().getStringArray(R.array.s_country_codes);
        for (int i = 0; i < countryCodes.length; i++) {
            if (phoneNumber.contains(countryCodes[i])) {
                tvCountryCodeNumber.setText(countryCodes[i]);
                tvPhoneNumber.setText(phoneNumber.replace(countryCodes[i], ""));
                break;
            }
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            Date selected = new Date(calendar.getTimeInMillis());
            editedUser.setBirthdate(selected);

            Formatter formatter = new Formatter(new Locale("es", "ES"));
            formatter.format("%tB %td %tY", calendar, calendar, calendar);
            tvBirthdate.setText(formatter.toString());
        }
    };

    public void saveChanges() {
        setMainButtonsVisibility(View.VISIBLE);
        toggleEnabledAttribute(R.id.layout_edit_profile, false);
        findViewById(R.id.layout_edit_profile).setVisibility(View.VISIBLE);
        setTitle(R.string.title_activity_profile);
        saveChanges.setVisibility(View.GONE);

        if (editedUser.compareDetails(user))
            return;

        if (!new NetworkHelper(context).hasNetworkConnection()) {
            Toast.makeText(context, context.getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("user", editedUser.toString());
        params.put("operation", String.valueOf(DETAILS));

        OkHttpServletRequest request = new OkHttpServletRequest(context);
        Request builtRequest = request.buildRequest(Servlets.USER, RequestType.PUT, params);
        OkHttpClient client = request.buildClient();
        client.newCall(builtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                processChangesResults(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                processChangesResults(response.body().string());
            }
        });
    }

    private void processChangesResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String message = context.getString(R.string.error_server);
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        message = getString(R.string.msj_profile_changes);
                        User user = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setDateFormat("yyyy-MM-dd").create().fromJson(json.get("data").getAsString(), User.class);
                        setUser(user);
                        setData();
                    }
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void activateSaveButton(View v) {
        setMainButtonsVisibility(View.GONE);
        if (v.getId() == R.id.fbtn_edit_profile) {
            setTitle(R.string.title_activity_edit_profile);
            toggleEnabledAttribute(R.id.layout_edit_profile, true);
            findViewById(R.id.layout_edit_profile).setVisibility(View.VISIBLE);
        }
        saveChanges.setTag(v.getId());
        saveChanges.setVisibility(View.VISIBLE);
    }

    public void toggleEnabledAttribute(int layout, boolean visibility) {
        LinearLayout editProfileLayout = (LinearLayout) findViewById(layout);

        for (int i = 0; i < editProfileLayout.getChildCount(); i++) {
            editProfileLayout.getChildAt(i).setEnabled(visibility);
        }
        tvBirthdate.setEnabled(visibility);
        tvCountryCodeNumber.setEnabled(visibility);
        tvPhoneNumber.setEnabled(visibility);
        imgBtnBirthdate.setEnabled(visibility);
    }

    public void setMainButtonsVisibility(int visibility) {
        FloatingActionButton fltngBtnModifuDocument = (FloatingActionButton)
                findViewById(R.id.fbtn_modify_documents);
        fltngBtnModifuDocument.setVisibility(visibility);
        fltngBtnModifuDocument.setOnClickListener(document);
        findViewById(R.id.fbtn_edit_profile).setVisibility(visibility);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem changePassword = menu.findItem(R.id.menu_option_01);
        changePassword.setTitle(getString(R.string.mn_item_change_password));
        changePassword.setOnMenuItemClickListener(new ChangePasswordDialog(ProfileActivity.this, user));

        MenuItem itemDeactivateAccount = menu.findItem(R.id.menu_option_02);
        itemDeactivateAccount.setTitle(getString(R.string.mn_item_profile_deactivate));
        itemDeactivateAccount.setOnMenuItemClickListener(deactivateAccount);
        menu.removeItem(R.id.menu_option_03);

        return super.onPrepareOptionsMenu(menu);
    }

    private FloatingActionButton.OnClickListener document = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this, DocumentsActivity.class);
            finish();
            startActivity(intent);
        }
    };

    private MenuItem.OnMenuItemClickListener deactivateAccount = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (!new NetworkHelper(context).hasNetworkConnection()) {
                Toast.makeText(context, context.getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
                return false;
            }

            Map<String, String> params = new HashMap<>();
            params.put("user", user.toString());
            params.put("operation", String.valueOf(DISABLE));

            OkHttpServletRequest request = new OkHttpServletRequest(context);
            Request builtRequest = request.buildRequest(Servlets.USER, RequestType.PUT, params);
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    processDeactivateResults(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    processDeactivateResults(response.body().string());
                }
            });
            return true;
        }
    };

    private void processDeactivateResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String message = context.getString(R.string.error_server);
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        message = getString(R.string.msj_account_deactivated);
                        logout();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
