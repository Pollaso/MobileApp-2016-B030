package ipn.mobileapp.model.service.dao.user;

public interface IUserSchema {
    String USER_TABLE = "users";
    String COLUMN_ID = "id";
    String COLUMN_EMAIL = "email";
    String COLUMN_NAME = "name";
    String COLUMN_PATERNAL_SURNAME = "paternal_surname";
    String COLUMN_MATERNAL_SURNAME = "maternal_surname";
    String COLUMN_PHONE_NUMBER = "phone_number";
    String COLUMN_BIRTHDATE = "birthdate";
    String COLUMN_ROLE = "role";
    String COLUMN_USER_ID = "user_id";
    String COLUMN_ENABLED = "enabled";
    String USER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE
            + " ("
            + COLUMN_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PATERNAL_SURNAME + " TEXT NOT NULL, "
            + COLUMN_MATERNAL_SURNAME + " TEXT NOT NULL, "
            + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
            + COLUMN_BIRTHDATE + " TEXT NOT NULL, "
            + COLUMN_ROLE + " TEXT NOT NULL, "
            + COLUMN_USER_ID + " TEXT, "
            + COLUMN_ENABLED + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COLUMN_ID + ")"
            + ")";

    String[] USER_COLUMNS = new String[]{COLUMN_ID, COLUMN_EMAIL, COLUMN_NAME, COLUMN_PATERNAL_SURNAME, COLUMN_MATERNAL_SURNAME, COLUMN_PHONE_NUMBER, COLUMN_BIRTHDATE, COLUMN_ROLE, COLUMN_USER_ID, COLUMN_ENABLED};
}
