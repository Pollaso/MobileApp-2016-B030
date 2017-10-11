package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.util.Collection;
import java.util.Date;

import ipn.mobileapp.model.service.dao.user.IUserSchema;

import static ipn.mobileapp.model.service.dao.user.IUserSchema.USER_TABLE;

@DatabaseTable(tableName = USER_TABLE)
public class User implements IUserSchema {
    public final static String USER_ROLE = "USER";
    public final static String SUBUSER_ROLE = "SUB";

    @DatabaseField(columnName = COLUMN_ID, id = true)
    private String id;
    @DatabaseField(columnName = COLUMN_EMAIL, unique = true)
    private String email;
    private String password;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_PATERNAL_SURNAME)
    private String paternalSurname;
    @DatabaseField(columnName = COLUMN_MATERNAL_SURNAME)
    private String maternalSurname;
    @DatabaseField(columnName = COLUMN_PHONE_NUMBER, unique = true)
    private String phoneNumber;
    @DatabaseField(columnName = COLUMN_BIRTHDATE, dataType = DataType.DATE_STRING)
    private Date birthdate;
    @DatabaseField(columnName = COLUMN_ROLE)
    private String role;
    @DatabaseField(columnName = COLUMN_USER_ID, columnDefinition = "STRING REFERENCES users(id)")
    private String userId;
    @DatabaseField(columnName = COLUMN_ENABLED, dataType = DataType.BOOLEAN_INTEGER)
    private boolean enabled;

    private Collection<User> subUsers = null;
    private Collection<Vehicle> vehicles = null;
    private Collection<Document> documents = null;
    private Collection<Contact> contacts = null;
    private Collection<AlcoholTest> alcoholTests = null;

    public User() {
    }

    @Override
    public String toString() {
        if (birthdate != null)
            return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setDateFormat("yyyy-MM-dd").create().toJson(this);
        return new Gson().toJson(this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Collection<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Collection<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Collection<Document> documents) {
        this.documents = documents;
    }

    public Collection<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Collection<Contact> contacts) {
        this.contacts = contacts;
    }

    public Collection<AlcoholTest> getAlcoholTests() {
        return alcoholTests;
    }

    public void setAlcoholTests(Collection<AlcoholTest> alcoholTests) {
        this.alcoholTests = alcoholTests;
    }

    public Collection<User> getSubUsers() {
        return subUsers;
    }

    public void setSubUsers(Collection<User> subUsers) {
        this.subUsers = subUsers;
    }

    public boolean compareDetails(User user) {
        return this.email.equals(user.getEmail()) && this.name.equals(user.getName()) && this.paternalSurname.equals(user.getPaternalSurname()) && this.maternalSurname.equals(getMaternalSurname()) && this.birthdate.equals(user.getBirthdate());
    }
}