package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Contact {
    private String contactId;
    private String phoneNumber;
    private String name;
    private String paternalSurname;
    private String maternalSurname;
    private String profileImage;
    private String userId;

    public Contact() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
