package ipn.mobileapp.model.service.dao.contact;

import ipn.mobileapp.model.service.dao.user.IUserSchema;

public interface IContactSchema {
    String CONTACT_TABLE = "users";
    String COLUMN_ID = "id";
    String COLUMN_PHONE_NUMBER = "phone_number";
    String COLUMN_NAME = "name";
    String COLUMN_PATERNAL_SURNAME = "paternal_surname";
    String COLUMN_MATERNAL_SURNAME = "maternal_surname";
    String COLUMN_USER_ID = "user_id";
    String CONTACT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE
            + " ("
            + COLUMN_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PATERNAL_SURNAME + " TEXT NOT NULL, "
            + COLUMN_MATERNAL_SURNAME + " TEXT NOT NULL, "
            + COLUMN_USER_ID + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + IUserSchema.USER_TABLE + "(" + IUserSchema.COLUMN_ID + ")"
            + ")";

    String[] CONTACT_COLUMNS = new String[]{COLUMN_ID, COLUMN_PHONE_NUMBER, COLUMN_NAME, COLUMN_PATERNAL_SURNAME, COLUMN_MATERNAL_SURNAME, COLUMN_USER_ID};
}
