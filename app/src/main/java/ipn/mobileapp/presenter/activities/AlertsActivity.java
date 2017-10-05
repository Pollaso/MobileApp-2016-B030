package ipn.mobileapp.presenter.activities;

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
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.model.service.ServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.presenter.adapter.AlertAdapter;
import ipn.mobileapp.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertsActivity extends BaseActivity {
    private static final String SELECT_ALL = "SELECT_ALL";

    private ListView lvAlerts;
    private View contentView;
    private TextView tvEmpty;

    private ArrayList<Alert> alerts;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(R.layout.activity_list_view, null, false);
        drawer.addView(contentView, 0);

        SharedPreferencesManager manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        id = (String) manager.getValue("_id", String.class);

        getComponents();
        setComponentAttributes();
        getAlerts();
    }

    private void setListView() {
        ArrayList<Alert> alerts = new ArrayList<>();
        Alert alert = new Alert();

        if (alerts == null)
            alerts = new ArrayList<>();

        AlertAdapter adapter = new AlertAdapter(AlertsActivity.this, R.layout.listview_alert_item, alerts, dismissDialog);
        lvAlerts.setAdapter(adapter);
        if (lvAlerts.getCount() != 0)
            tvEmpty.setVisibility(View.GONE);
        else
            tvEmpty.setVisibility(View.VISIBLE);
    }

    private void getComponents() {
        lvAlerts = (ListView) findViewById(R.id.lv_items);
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.fltng_btn_add_item);
        add.setVisibility(View.GONE);
        tvEmpty = (TextView) findViewById(R.id.tv_empty_list_view);
    }

    private void setComponentAttributes() {
        tvEmpty.setText(R.string.msj_no_alerts);
    }

    public void getAlerts() {
        Map<String, String> params = new ArrayMap<>();
        params.put("expression", SELECT_ALL);
        params.put("id", id);

        ServletRequest request = new ServletRequest(AlertsActivity.this);
        Request builtRequest = request.buildRequest(Servlets.ALERT, RequestType.GET, params);
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
                        TypeToken type = new TypeToken<ArrayList<Alert>>() {
                        };
                        alerts = new Gson().fromJson(json.get("data").getAsString(), type.getType());
                    } else if (json.has("warnings")) {
                        alerts = null;
                    }
                    setListView();
                } else
                    Toast.makeText(AlertsActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    private DialogInterface.OnDismissListener dismissDialog = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            getAlerts();
        }
    };
}

