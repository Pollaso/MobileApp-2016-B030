package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.User;

public class SubUserAdapter extends ArrayAdapter<User> {
    private Context context;
    private int resource;

    public SubUserAdapter(@NonNull Context context, @NonNull @LayoutRes int resource, @NonNull ArrayList<User> subUsers) {
        super(context, resource, subUsers);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.iv_profile_image);

        TextView tvFullName = (TextView) convertView.findViewById(R.id.txtv_full_name);
        String fullName = user.getName() + " " + user.getPaternalSurname() + " " + user.getMaternalSurname();
        tvFullName.setText(fullName);

        TextView tvPhoneNumber = (TextView) convertView.findViewById(R.id.txtv_phone_number);
        tvPhoneNumber.setText(user.getPhoneNumber());

        TextView tvVehicle = (TextView) convertView.findViewById(R.id.txtv_vehicle);

        TextView tvLastAlcoholicState = (TextView) convertView.findViewById(R.id.tv_last_alcoholic_state);

        return convertView;
    }
}
