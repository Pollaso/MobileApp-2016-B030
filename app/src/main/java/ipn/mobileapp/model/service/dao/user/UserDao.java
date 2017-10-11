package ipn.mobileapp.model.service.dao.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ipn.mobileapp.model.service.dao.DatabaseContentProvider;
import ipn.mobileapp.model.pojo.User;

public class UserDao extends DatabaseContentProvider
        implements IUserSchema, IUserDao {

    public UserDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public User findById(String _id) {
        final String selectionArgs[] = {_id};
        final String selection = COLUMN_ID + " = ?";
        User user = new User();
        Cursor cursor = super.query(USER_TABLE, USER_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                user = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return user;
    }

    @Override
    public List<User> findByUserId(String userId) {
        List<User> users = new ArrayList<User>();
        final String selectionArgs[] = {userId};
        final String selection = COLUMN_ID + " = ?";
        Cursor cursor = super.query(USER_TABLE, USER_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursorToEntity(cursor);
                users.add(user);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return users;
    }

    @Override
    public boolean insert(User user) {
        ContentValues values = setContentValue(user);
        try {
            return super.insert(USER_TABLE, values) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean insert(List<User> users) {
        return false;
    }

    @Override
    public boolean update(User user) {
        final String selectionArgs[] = {user.getId()};
        final String selection = COLUMN_ID + " = ?";
        ContentValues values = setContentValue(user);
        try {
            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete() {
        try {
            return super.delete(USER_TABLE, null, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String _id) {
        final String selectionArgs[] = {_id};
        final String selection = COLUMN_ID + " = ?";
        try {
            return super.delete(USER_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    protected User cursorToEntity(Cursor cursor) {
        User user = new User();

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                user.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            }
            if (cursor.getColumnIndex(COLUMN_EMAIL) != -1) {
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            }
            if (cursor.getColumnIndex(COLUMN_PATERNAL_SURNAME) != -1) {
                user.setPaternalSurname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATERNAL_SURNAME)));
            }
            if (cursor.getColumnIndex(COLUMN_MATERNAL_SURNAME) != -1) {
                user.setMaternalSurname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATERNAL_SURNAME)));
            }
            if (cursor.getColumnIndex(COLUMN_PHONE_NUMBER) != -1) {
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)));
            }
            if (cursor.getColumnIndex(COLUMN_BIRTHDATE) != -1) {
                user.setBirthdate(Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDATE))));
            }
            if (cursor.getColumnIndex(COLUMN_ROLE) != -1) {
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            }
            if (cursor.getColumnIndex(COLUMN_USER_ID) != -1)
                user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
        }
        if (cursor.getColumnIndex(COLUMN_ENABLED) != -1) {
            user.setEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENABLED)) > 0);
        }
        return user;
    }

    private ContentValues setContentValue(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.getId());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PATERNAL_SURNAME, user.getPaternalSurname());
        values.put(COLUMN_MATERNAL_SURNAME, user.getMaternalSurname());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_BIRTHDATE, String.valueOf(user.getBirthdate()));
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_ENABLED, user.isEnabled());

        return values;
    }
}
