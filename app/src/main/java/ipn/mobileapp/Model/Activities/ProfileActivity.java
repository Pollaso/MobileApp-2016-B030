package ipn.mobileapp.Model.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ScrollingView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import ipn.mobileapp.Model.Components.VehicleDialog;
import ipn.mobileapp.R;

public class ProfileActivity extends BaseActivity {
    private FloatingActionButton saveChanges;
    private FloatingActionButton addVehicle;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawer.addView(contentView, 0);

        findViewById(R.id.txtv_vehicle).setEnabled(false);

        final ScrollView profileScroll = (ScrollView) findViewById(R.id.sv_profile_scroll);
        profileScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (saveChanges.getVisibility() != View.GONE) {
                    setMainButtonsVisibility(View.GONE);
                } else {
                    if (profileScroll.getScrollY() != 0) {
                        setMainButtonsVisibility(View.GONE);
                    } else {
                        setMainButtonsVisibility(View.VISIBLE);
                    }
                }
            }
        });

        saveChanges = (FloatingActionButton) drawer.findViewById(R.id.fbtn_save_changes);
        addVehicle = (FloatingActionButton) drawer.findViewById(R.id.fbtn_add_vehicle);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        addVehicle.setOnClickListener(new VehicleDialog(context));
    }

    public void saveChanges() {
        setMainButtonsVisibility(View.VISIBLE);
        if (saveChanges.getTag().equals(R.id.fbtn_modify_documents)) {
            toggleEnabledAttribute(R.id.layout_modify_documents, false);
        } else {
            toggleEnabledAttribute(R.id.layout_edit_profile, false);
        }
        findViewById(R.id.layout_edit_profile).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_modify_documents).setVisibility(View.GONE);
        setTitle(R.string.title_activity_profile);
        saveChanges.setVisibility(View.GONE);
        saveChanges.setTag("");
    }

    public void activateSaveButton(View v) {
        setMainButtonsVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.fbtn_modify_documents:
                setTitle(R.string.title_activity_documents);
                findViewById(R.id.layout_edit_profile).setVisibility(View.GONE);
                findViewById(R.id.layout_modify_documents).setVisibility(View.VISIBLE);
                toggleEnabledAttribute(R.id.layout_modify_documents, true);
                break;
            case R.id.fbtn_edit_profile:
                setTitle(R.string.title_activity_edit_profile);
                findViewById(R.id.layout_edit_profile).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_modify_documents).setVisibility(View.GONE);
                toggleEnabledAttribute(R.id.layout_edit_profile, true);
                break;
        }
        saveChanges.setTag(v.getId());
        saveChanges.setVisibility(View.VISIBLE);
    }

    public void toggleEnabledAttribute(int layout, boolean visibility) {
        LinearLayout editProfileLayout = (LinearLayout) findViewById(layout);

        for (int i = 0; i < editProfileLayout.getChildCount(); i++) {
            editProfileLayout.getChildAt(i).setEnabled(visibility);
        }
    }

    public void setMainButtonsVisibility(int visibility) {
        findViewById(R.id.fbtn_modify_documents).setVisibility(visibility);
        findViewById(R.id.fbtn_edit_profile).setVisibility(visibility);
        findViewById(R.id.fbtn_add_vehicle).setVisibility(visibility);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.menu_option_01);
        menuItem.setTitle(getString(R.string.mn_profile_deactivate));
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(context, R.string.msj_account_deactivated, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
        menu.removeItem(R.id.menu_option_02);
        menu.removeItem(R.id.menu_option_03);

        return super.onPrepareOptionsMenu(menu);
    }
}
