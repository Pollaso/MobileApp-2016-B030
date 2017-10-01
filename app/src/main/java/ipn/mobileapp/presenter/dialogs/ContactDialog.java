package ipn.mobileapp.presenter.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JsonUtils;
import ipn.mobileapp.model.pojo.Contact;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ContactDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private DialogInterface.OnDismissListener dismissListener;

    private EditText etName;
    private EditText etPaternalSurname;
    private EditText etMaternalSurname;
    private Spinner spnrCountryCode;
    private TextView tvCountryCodeNumber;
    private EditText etPhoneNumber;

    private Button btnAddContact;
    private Button btnCancel;

    private Contact contact;

    public ContactDialog(Context context, Contact contact, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        this.contact = contact;
        this.dismissListener = dismissListener;
    }

    /*ContactsManager contactsManager = new ContactsManager(this);
        contactsManager.getContacts();
        List<String> phone = new ArrayList<String>();
        phone.add("+19095985621");
        contactsManager.addContact("Benjamin", "Clementine", "", phone);*/

    @Override
    public void onClick(View v) {
        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.dialog_contacts);
        Button saveContact = (Button) openDialog.findViewById(R.id.btn_save_edit);
        if (contact != null) {
            openDialog.setTitle(context.getString(R.string.title_dialog_edit_contact));
            saveContact.setText(context.getString(R.string.btn_save_changes));
        } else {
            openDialog.setTitle(context.getString(R.string.title_dialog_add_contact));
            saveContact.setText(context.getString(R.string.btn_save_contact));
        }
        openDialog.show();

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (contact != null)
                    Toast.makeText(context, R.string.msj_contact_saved, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, R.string.msj_contact_added, Toast.LENGTH_LONG).show();
                openDialog.dismiss();
            }
        });
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
        btnAddContact = (Button) dialog.findViewById(R.id.btn_register);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        etName = (EditText) dialog.findViewById(R.id.et_name);
        etPaternalSurname = (EditText) dialog.findViewById(R.id.et_paternal_surname);
        etMaternalSurname = (EditText) dialog.findViewById(R.id.et_maternal_surname);
        spnrCountryCode = (Spinner) dialog.findViewById(R.id.s_country_code);
        tvCountryCodeNumber = (TextView) dialog.findViewById(R.id.txtv_country_code);
        etPhoneNumber = (EditText) dialog.findViewById(R.id.et_phone_number);
    }

    private void setComponentAttributes() {
        btnAddContact.setText(context.getString(R.string.btn_register_sub_user));
        btnAddContact.setOnClickListener(register);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etName, etPaternalSurname, etMaternalSurname, tvCountryCodeNumber, etPhoneNumber};

        etName.addTextChangedListener(new TextValidator(etName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidName(text))
                    etName.setError(context.getString(R.string.warning_name));
                else
                    btnAddContact.setEnabled(validator.validateFields(fields));
            }
        });
        etPaternalSurname.addTextChangedListener(new TextValidator(etPaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etPaternalSurname.setError(context.getString(R.string.warning_surname));
                else
                    btnAddContact.setEnabled(validator.validateFields(fields));
            }
        });
        etMaternalSurname.addTextChangedListener(new TextValidator(etMaternalSurname) {
            @Override
            public void validate(TextView textView, String text) {
                if (!validator.isValidLastName(text))
                    etMaternalSurname.setError(context.getString(R.string.warning_surname));
                else
                    btnAddContact.setEnabled(validator.validateFields(fields));
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
                    btnAddContact.setEnabled(validator.validateFields(fields));
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
                    if (json.has("data")) {
                        Contact contact = new Gson().fromJson(json.get("data").getAsString(), Contact.class);

                        /* Using OrmLite */
                        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());
                        try {
                            /*final Dao<User, String> userDao = databaseHelper.getUserDao();
                            userDao.create(user);
                        } catch (SQLException e) {
                            e.printStackTrace();*/
                        } finally {
                            databaseHelper.close();
                        }

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

    private void createContact() {
        contact.setName(etName.getText().toString());
        contact.setPaternalSurname(etPaternalSurname.getText().toString());
        contact.setMaternalSurname(etMaternalSurname.getText().toString());
        contact.setPhoneNumber(tvCountryCodeNumber.getText().toString() + etPhoneNumber.getText().toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", MODE_PRIVATE);
        contact.setUserId(sharedPreferences.getString("_id", null));
    }

    private Button.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createContact();
            Map<String, String> params = new ArrayMap<>();
            params.put("user", contact.toString());

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
