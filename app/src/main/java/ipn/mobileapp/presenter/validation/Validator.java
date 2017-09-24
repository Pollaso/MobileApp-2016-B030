package ipn.mobileapp.presenter.validation;

import android.content.Context;
import android.util.Patterns;
import android.widget.TextView;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ipn.mobileapp.R;

public class Validator {
    private Context context;

    public Validator(Context context)
    {
        this.context = context;
    }

    public boolean isValidPassword(String password){
        final String PASSWORD_REGEX = context.getString(R.string.password_regex);

        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isValidUUID(String serialKey)
    {
        final String UUID_REGEX = context.getString(R.string.uuid_regex);

        Pattern pattern = Pattern.compile(UUID_REGEX);
        Matcher matcher = pattern.matcher(serialKey);
        return matcher.matches();
    }

    public boolean isValidName(String name)
    {
        final String NAME_REGEX = context.getString(R.string.name_regex);

        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public boolean isValidLastName(String lastName)
    {
        final String LAST_NAME_REGEX = context.getString(R.string.last_name_regex);

        Pattern pattern = Pattern.compile(LAST_NAME_REGEX);
        Matcher matcher = pattern.matcher(lastName);
        return matcher.matches();
    }

    public boolean isValidEmail(String name)
    {
        return Patterns.EMAIL_ADDRESS.matcher(name).matches();
    }

    public boolean isValidPhone(String phone)
    {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public  boolean isValidCarPlates(String carPlates){
        final String LAST_NAME_REGEX = context.getString(R.string.car_plate_regex);

        Pattern pattern = Pattern.compile(LAST_NAME_REGEX);
        Matcher matcher = pattern.matcher(carPlates);
        return matcher.matches();
    }

    public boolean validateFields(TextView[] fields) {
        for (int i = 0; i < fields.length; i++) {
            TextView currentField = fields[i];
            if (currentField.getText().toString().length() <= 0) {
                return false;
            }
        }
        return true;
    }
}
