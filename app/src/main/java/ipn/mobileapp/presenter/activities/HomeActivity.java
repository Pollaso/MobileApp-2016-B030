package ipn.mobileapp.presenter.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.Contact;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class HomeActivity extends BaseActivity {
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawer.addView(contentView, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        hasContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    public void hasContacts() {
        Map<String, String> params = new ArrayMap<>();
        params.put("expression", "SELECT_ALL");
        params.put("id", id);

        OkHttpServletRequest request = new OkHttpServletRequest(HomeActivity.this);
        Request builtRequest = request.buildRequest(Servlets.CONTACT, RequestType.GET, params);
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
                    if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        if (warnings.has("noneFound")) {
                            Intent intent = new Intent(getBaseContext(), ContactsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }
}
