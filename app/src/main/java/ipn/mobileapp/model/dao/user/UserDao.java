package ipn.mobileapp.model.dao.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ipn.mobileapp.model.DatabaseContentProvider;
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
        final String selectionArgs[] = {user.get_id()};
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
        return null;
    }

    private ContentValues setContentValue(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.get_id());
        values.put(COLUMN_EMAIL, user.getEmail());

        return values;
    }
}
