package ipn.mobileapp.view.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ipn.mobileapp.model.ContactsManager;
import ipn.mobileapp.view.dialogs.ContactDialog;
import ipn.mobileapp.R;

public class ContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_contacts, null, false);
        drawer.addView(contentView, 0);

        TextView editContact = (TextView)findViewById(R.id.edit_contact);
        FloatingActionButton addContact = (FloatingActionButton) findViewById(R.id.fbtn_add_contact);
        addContact.setOnClickListener(new ContactDialog(this, false));
        editContact.setOnClickListener(new ContactDialog(this, true));

        ContactsManager contactsManager = new ContactsManager(this);
        contactsManager.getContacts();
        List<String> phone = new ArrayList<String>();
        phone.add("+19095985621");
        contactsManager.addContact("Benjamin", "Clementine", "", phone);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }
}
