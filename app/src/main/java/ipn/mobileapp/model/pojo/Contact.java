package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_ID;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_MATERNAL_SURNAME;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_NAME;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_PATERNAL_SURNAME;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_PHONE_NUMBER;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.COLUMN_USER_ID;
import static ipn.mobileapp.model.service.dao.contact.IContactSchema.CONTACT_TABLE;

@DatabaseTable(tableName = CONTACT_TABLE)
public class Contact {
    @DatabaseField(columnName = COLUMN_ID, id = true)
    private String id;
    @DatabaseField(columnName = COLUMN_PHONE_NUMBER)
    private String phoneNumber;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_PATERNAL_SURNAME)
    private String paternalSurname;
    @DatabaseField(columnName = COLUMN_MATERNAL_SURNAME)
    private String maternalSurname;
    @DatabaseField(columnName = COLUMN_USER_ID, columnDefinition = "STRING REFERENCES users(id)")
    private String userId;

    public Contact() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact)) {
            return false;
        }
        Contact contact = (Contact)obj;
        return contact.getId().equals(getId());

    }

    public boolean hasNullFields(ArrayList<String> skippedFields, ArrayList<Class> validTypes) {
        for (Field field : getClass().getDeclaredFields())
            try {
                if (validTypes.contains(field.getType()) && skippedFields.contains(field.getName()) && field.get(this) != null)
                    return false;
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaternalSurname() {
        return paternalSurname;
    }

    public void setPaternalSurname(String paternalSurname) {
        this.paternalSurname = paternalSurname;
    }

    public String getMaternalSurname() {
        return maternalSurname;
    }

    public void setMaternalSurname(String maternalSurname) {
        this.maternalSurname = maternalSurname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
