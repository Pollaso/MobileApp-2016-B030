package ipn.mobileapp.presenter.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.helper.JsonUtils;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.presenter.adapter.SubUserAdapter;
import ipn.mobileapp.presenter.dialogs.SubUserDialog;
import ipn.mobileapp.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubUsersActivity extends BaseActivity {
    private static final String SELECT_ALL = "SELECT_ALL";

    private ListView lvSubUsers;
    private FloatingActionButton addSubUser;
    private View contentView;

    private ArrayList<User> subUsers;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(R.layout.activity_list_view, null, false);
        drawer.addView(contentView, 0);

        SharedPreferencesManager manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        id = (String) manager.getValue("_id", String.class);

        getComponents();
        setComponentAttributes();
        getSubUsers();
    }

    private void setListView() {
        if (subUsers == null)
            subUsers = new ArrayList<>();

        SubUserAdapter adapter = new SubUserAdapter(this, R.layout.listview_sub_user_item, subUsers);
        lvSubUsers.setAdapter(adapter);
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_list_view);
        tvEmpty.setText(R.string.msj_no_sub_users);
        if (lvSubUsers.getCount() == 0)
            tvEmpty.setVisibility(View.VISIBLE);
        else
            tvEmpty.setVisibility(View.GONE);
    }

    private void getComponents() {
        lvSubUsers = (ListView) findViewById(R.id.lv_items);
        addSubUser = (FloatingActionButton) findViewById(R.id.fltng_btn_add_item);
    }

    private void setComponentAttributes() {
        addSubUser.setOnClickListener(new SubUserDialog(this, dismissRegisterDialog));
    }

    public void getSubUsers() {
        Map<String, String> params = new ArrayMap<>();
        params.put("expression", SELECT_ALL);
        params.put("id", id);

        ServletRequest request = new ServletRequest(SubUsersActivity.this);
        Request builtRequest = request.buildRequest(Servlets.USER, RequestType.GET, params);
        OkHttpClient client = request.buildClient();
        client.newCall(builtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                processResults(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                processResults(response.body().string());
            }
        });
    }

    private void processResults(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        TypeToken type = new TypeToken<ArrayList<User>>() {
                        };
                        subUsers = new Gson().fromJson(json.get("data").getAsString(), type.getType());
                    } else if (json.has("warnings")) {
                        subUsers = null;
                    }
                    setListView();
                } else
                    Toast.makeText(SubUsersActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    private DialogInterface.OnDismissListener dismissRegisterDialog = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            getSubUsers();
        }
    };
}
