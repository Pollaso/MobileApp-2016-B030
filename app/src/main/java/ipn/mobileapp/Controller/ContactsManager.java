package ipn.mobileapp.Controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;


import java.util.ArrayList;
import java.util.List;

import android.provider.ContactsContract;
import android.database.Cursor;
import android.net.Uri;

public class ContactsManager {
    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    Context context = null;
    List<Contact> contacts;

    public ContactsManager(Context context) {
        this.context = context;
    }

    public List<Contact> getContacts() {
        contacts = new ArrayList<Contact>();

        String PHOTO_THUMBNAIL_URI = Contacts.PHOTO_THUMBNAIL_URI;
        String _ID = Contacts._ID;
        String DISPLAY_NAME = Contacts.DISPLAY_NAME;
        String DISPLAY_NAME_PRIMARY = Contacts.DISPLAY_NAME_PRIMARY;
        String DISPLAY_NAME_ALTERNATIVE = Contacts.DISPLAY_NAME_ALTERNATIVE;
        String HAS_PHONE_NUMBER = Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = Phone.CONTENT_URI;
        String Phone_CONTACT_ID = Phone.CONTACT_ID;
        String NUMBER = Phone.NUMBER;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        List<String> phoneNumbers = null;
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contactId = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                String primary = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY));
                String alternative = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_ALTERNATIVE));
                String photoURI = cursor.getString(cursor.getColumnIndex(PHOTO_THUMBNAIL_URI));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contactId}, null);
                    phoneNumbers = new ArrayList<String>();
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phoneNumbers.add(phoneNumber);
                    }
                    phoneCursor.close();
                }
                contacts.add(new Contact(photoURI, contactId, name, phoneNumbers));

            }
        }

        return contacts;
    }

    public void addContact(String firstName, String lastNameFather, String lastNameMother, List<String> phoneNumbers) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
        operationList.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.GIVEN_NAME, firstName)
                .withValue(StructuredName.FAMILY_NAME, lastNameFather + " " + lastNameMother)
                .build());

        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, phoneNumbers.get(0))
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build());
        try{
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void editContact() {

    }
}
