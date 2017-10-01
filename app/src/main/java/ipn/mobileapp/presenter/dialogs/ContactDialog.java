package ipn.mobileapp.presenter.dialogs;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JsonUtils;
import ipn.mobileapp.model.pojo.Contact;
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
    private TextView tvDeleteContact;

    private Button btnSaveContact;
    private Button btnCancel;

    private Contact contact;
    private Crud mode;
    private Boolean udMode;

    public ContactDialog(Context context, Contact contact, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        this.dismissListener = dismissListener;
        udMode = contact != null;
        if (udMode) {
            mode = Crud.DELETE;
            Field[] fields = contact.getClass().getDeclaredFields();
            for (int i = 1; i < fields.length; i++)
                if (fields[i] != null)
                    mode = Crud.UPDATE;
        }
        this.contact = udMode ? contact : new Contact();
    }

    /*ContactsManager contactsManager = new ContactsManager(this);
        contactsManager.getContacts();
        List<String> phone = new ArrayList<String>();
        phone.add("+19095985621");
        contactsManager.addContact("Benjamin", "Clementine", "", phone);*/

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
    }

    private void createDialog() {
        String title = context.getString(R.string.title_dialog_add_contact);

        if (udMode)
            title = mode == Crud.UPDATE ? context.getString(R.string.title_dialog_edit_contact) : context.getString(R.string.title_dialog_delete_contact);

        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_contacts)
                .setTitle(title)
                .setCancelable(true)
                .setOnDismissListener(dismissListener)
                .create();
        dialog.show();
    }

    private void getComponents() {
        btnSaveContact = (Button) dialog.findViewById(R.id.btn_save_contact);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        etName = (EditText) dialog.findViewById(R.id.et_name);
        etPaternalSurname = (EditText) dialog.findViewById(R.id.et_paternal_surname);
        etMaternalSurname = (EditText) dialog.findViewById(R.id.et_maternal_surname);
        spnrCountryCode = (Spinner) dialog.findViewById(R.id.s_country_code);
        tvCountryCodeNumber = (TextView) dialog.findViewById(R.id.txtv_country_code);
        etPhoneNumber = (EditText) dialog.findViewById(R.id.et_phone_number);
        tvDeleteContact = (TextView) dialog.findViewById(R.id.tv_delete_contact);
    }

    private void setComponentAttributes() {
        String saveContactBtnText = context.getString(R.string.btn_save_contact);

        if (udMode)
            saveContactBtnText = mode == Crud.UPDATE ? context.getString(R.string.btn_save_changes) : context.getString(R.string.btn_delete);

        btnSaveContact.setText(saveContactBtnText);
        btnSaveContact.setOnClickListener(saveContact);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (udMode && mode == Crud.UPDATE) {
            etName.setText(contact.getName());
            etPaternalSurname.setText(contact.getPaternalSurname());
            etMaternalSurname.setText(contact.getMaternalSurname());
            String phoneNumber = contact.getPhoneNumber();
            String[] countryCodes = context.getResources().getStringArray(R.array.s_country_codes);
            for (int i = 0; i < countryCodes.length; i++) {
                if (phoneNumber.contains(countryCodes[i])) {
                    spnrCountryCode.setSelection(i);
                    etPhoneNumber.setText(phoneNumber.replace(countryCodes[i], ""));
                    break;
                }
            }
        }

        final TextView[] fields = new TextView[]{etName, etPaternalSurname, etMaternalSurname, tvCountryCodeNumber, etPhoneNumber};
        if (mode != Crud.DELETE) {
            final Validator validator = new Validator(context);

            etName.addTextChangedListener(new TextValidator(etName) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validator.isValidName(text))
                        etName.setError(context.getString(R.string.warning_name));
                    else {
                        contact.setName(text);
                        btnSaveContact.setEnabled(validator.validateFields(fields));
                    }
                }
            });
            etPaternalSurname.addTextChangedListener(new TextValidator(etPaternalSurname) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validator.isValidLastName(text))
                        etPaternalSurname.setError(context.getString(R.string.warning_surname));
                    else {
                        contact.setPaternalSurname(text);
                        btnSaveContact.setEnabled(validator.validateFields(fields));
                    }
                }
            });
            etMaternalSurname.addTextChangedListener(new TextValidator(etMaternalSurname) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validator.isValidLastName(text))
                        etMaternalSurname.setError(context.getString(R.string.warning_surname));
                    else {
                        contact.setMaternalSurname(text);
                        btnSaveContact.setEnabled(validator.validateFields(fields));
                    }
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
                    final String phoneNumber = tvCountryCodeNumber.getText().toString() + text;
                    if (!validator.isValidPhone(phoneNumber))
                        etPhoneNumber.setError(context.getString(R.string.warning_phone_number));
                    else {
                        contact.setPhoneNumber(phoneNumber);
                        btnSaveContact.setEnabled(validator.validateFields(fields));
                    }
                }
            });
        } else {
            for (TextView field : fields)
                field.setVisibility(View.GONE);
            tvDeleteContact.setVisibility(View.VISIBLE);
        }
    }

    private void processResults(final String response) {
        dialog.dismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        //Contact contact = new Gson().fromJson(json.get("data").getAsString(), Contact.class);
                        String message = udMode ? context.getString(R.string.msj_contact_saved) : context.getString(R.string.msj_contact_added);
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        if (warnings.has("noneInserted") || warnings.has("notAllInserted"))
                            Toast.makeText(context, context.getString(R.string.warning_contact_not_inserted), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button.OnClickListener saveContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            contact.setProfileImage(null);
            if (!udMode) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", MODE_PRIVATE);
                contact.setUserId(sharedPreferences.getString("_id", null));
            }

            ArrayList<Contact> contacts = new ArrayList<>();
            contacts.add(contact);
            Map<String, String> params = new ArrayMap<>();
            params.put("emergencyContacts", new Gson().toJson(contacts));

            ServletRequest request = new ServletRequest(context);
            Request builtRequest = request.buildRequest(Servlets.EMERGENCY_CONTACT, udMode ? RequestType.PUT : RequestType.POST, params);
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
