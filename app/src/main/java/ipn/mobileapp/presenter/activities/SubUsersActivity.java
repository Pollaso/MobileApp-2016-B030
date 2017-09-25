package ipn.mobileapp.presenter.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;

import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.DatabaseHelper;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.service.dao.user.IUserSchema;
import ipn.mobileapp.presenter.adapter.SubUserAdapter;
import ipn.mobileapp.presenter.dialogs.SubUserDialog;
import ipn.mobileapp.R;

public class SubUsersActivity extends BaseActivity {
    private final int ADD = 1;

    private ListView lvSubUsers;
    private FloatingActionButton addSubUser;

    private ArrayList<User> subUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_sub_users, null, false);
        drawer.addView(contentView, 0);

        getComponents();
        setComponentAttributes();
    }

    private void getComponents() {
        lvSubUsers = (ListView) findViewById(R.id.lv_sub_users);
        addSubUser = (FloatingActionButton) findViewById(R.id.fltng_btn_register_sub_user);
    }

    private void setComponentAttributes() {
        subUsers = getSubUsers();

        if (subUsers == null)
            subUsers = new ArrayList<>();

        SubUserAdapter adapter = new SubUserAdapter(this, R.layout.listview_sub_user_item, subUsers);
        lvSubUsers.setAdapter(adapter);
        if (lvSubUsers.getCount() == 0)
            findViewById(R.id.tv_empty_sub_users).setVisibility(View.VISIBLE);
        addSubUser.setOnClickListener(new SubUserDialog(this));
    }

    public ArrayList<User> getSubUsers() {
        ArrayList<User> subUsers = null;

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        try {
            SharedPreferencesManager manager = new SharedPreferencesManager(SubUsersActivity.this, getString(R.string.current_user_filename));

            final Dao<User, String> userDao = databaseHelper.getUserDao();
            QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(manager.getValue("_id", String.class));
            Where<User, String> where = queryBuilder.where();
            where.eq(IUserSchema.COLUMN_USER_ID, selectArg);
            PreparedQuery<User> preparedQuery = queryBuilder.prepare();
            subUsers = (ArrayList<User>) userDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }

        return subUsers;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }
}
