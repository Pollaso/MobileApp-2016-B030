package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Contact;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.presenter.dialogs.ContactDialog;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private Context context;
    private int resource;
    private DialogInterface.OnDismissListener dismissListener;

    public ContactAdapter(@NonNull Context context, @NonNull @LayoutRes int resource, @NonNull ArrayList<Contact> contacts, DialogInterface.OnDismissListener dismissListener) {
        super(context, resource, contacts);
        this.context = context;
        this.resource = resource;
        this.dismissListener = dismissListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(context);
            convertView = inflator.inflate(resource, parent, false);
        }

        ImageButton imgBtnEditContact = (ImageButton) convertView.findViewById(R.id.img_btn_edit_contact);
        imgBtnEditContact.setOnClickListener(new ContactDialog(context, contact, dismissListener));

        TextView tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
        String fullName = contact.getName() + " " + contact.getPaternalSurname() + " " + contact.getMaternalSurname();
        tvContactName.setText(fullName);

        TextView tvContactPhoneNumber = (TextView) convertView.findViewById(R.id.tv_contact_phone);
        tvContactPhoneNumber.setText(contact.getPhoneNumber());

        return convertView;
    }
}
