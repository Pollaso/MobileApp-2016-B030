package ipn.mobileapp.presenter.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import ipn.mobileapp.presenter.dialogs.SubUserDialog;
import ipn.mobileapp.R;

public class SubUsersActivity extends BaseActivity {
    private final int ADD = 1;
    private Menu menu;
    private FloatingActionButton addSubUser;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_sub_users, null, false);
        drawer.addView(contentView, 0);

        addSubUser = (FloatingActionButton)findViewById(R.id.fbtn_register_sub_user);
        addSubUser.setOnClickListener(new SubUserDialog(context, getSupportFragmentManager()));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }
}
