package ipn.mobileapp.model.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ipn.mobileapp.model.dao.user.IUserSchema;
import ipn.mobileapp.model.dao.user.UserDao;

public class Database {
    private static final String DATABASE_NAME = "dacba";
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper databaseHelper;
    private final Context context;
    public static UserDao userDao;


    public Database open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        userDao = new UserDao(database);

        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public void clear(){
        userDao.delete();
    }

    public Database(Context context) {
        this.context = context;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IUserSchema.USER_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "
                    + IUserSchema.USER_TABLE);
            onCreate(db);
        }
    }
}
