package ipn.mobileapp.presenter.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.presenter.adapter.SubUserAdapter;
import ipn.mobileapp.presenter.dialogs.SubUserDialog;
import ipn.mobileapp.R;

public class SubUsersActivity extends BaseActivity {
    private final int ADD = 1;

    private ListView lvSubUsers;
    private FloatingActionButton addSubUser;

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
        ArrayList<User> users = new ArrayList<>();
        SubUserAdapter adapter = new SubUserAdapter(this, R.layout.listview_sub_user_item, users);
        lvSubUsers.setAdapter(adapter);
        addSubUser.setOnClickListener(new SubUserDialog(this));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }
}
