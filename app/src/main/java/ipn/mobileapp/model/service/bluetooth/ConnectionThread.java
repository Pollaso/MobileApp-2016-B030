package ipn.mobileapp.model.service.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.AlcoholTest;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.model.pojo.Coordinate;
import ipn.mobileapp.model.pojo.Location;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.GpsService;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.activities.ContactsActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class ConnectionThread extends Thread {
    private Context context;
    private User user;
    private BluetoothSocket socket;

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectionThread(BluetoothSocket socket, Context context, User user) {
        this.context = context;
        this.user = user;
        this.socket = socket;

        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;

        try {
            tempInputStream = socket.getInputStream();
            tempOutputStream = socket.getOutputStream();
        } catch (IOException e) {
        }

        this.inputStream = tempInputStream;
        this.outputStream = tempOutputStream;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        while (true) {
            try {
                String input = "";
                do {
                    bytes = inputStream.read(buffer);
                    input += new String(buffer, 0, bytes);
                } while (!input.contains("\n"));

                final String msj = input;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        AlcoholTest alcoholTest = new AlcoholTest();

                        Calendar calendar = Calendar.getInstance();
                        alcoholTest.setOcurrence(new Date(calendar.getTimeInMillis()));

                        GpsService gpsService = new GpsService(context);
                        Coordinate coordinate = new Coordinate();
                        coordinate.setLatitude(gpsService.getLatitude());
                        coordinate.setLongitude(gpsService.getLongitude());
                        alcoholTest.setCoordinate(coordinate);

                        Location location = gpsService.getAddress();
                        alcoholTest.setLocation(location);

                        String ppmStr = msj.split("\r\n")[0].replace('\r', ' ').replace('\n', ' ').trim();
                        int ppm = Integer.valueOf(ppmStr);
                        alcoholTest.setAlcoholicState(ppm);

                        alcoholTest.setUserId(user.getId());

                        Alert alert = new Alert(alcoholTest, user);

                        Map<String, String> params = new ArrayMap<>();
                        params.put("alcoholTest", alcoholTest.toString());
                        OkHttpServletRequest request = new OkHttpServletRequest(context);
                        Request builtRequest = request.buildRequest(Servlets.ALCOHOL_TEST, RequestType.POST, params);
                        OkHttpClient client = request.buildClient();
                        client.newCall(builtRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                processResults(null, true);
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                processResults(response.body().string(), true);
                            }
                        });

                        if (alert.getUserId() != null) {
                            params.clear();
                            params.put("alert", alert.toString());
                            builtRequest = request.buildRequest(Servlets.ALERT, RequestType.POST, params);
                            client = request.buildClient();
                            client.newCall(builtRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    processResults(null, false);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    processResults(response.body().string(), false);
                                }
                            });
                        }

                        Intent intent = new Intent(context, ContactsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("alcoholTest", alcoholTest.toString());
                        context.startActivity(intent);
                    }
                });

            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(String input) {
        byte[] bytes = input.getBytes();
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Falla de conexión con el dispositivo", Toast.LENGTH_LONG).show();
        }
    }

    private void processResults(final String response, final boolean alcoholTest) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String message = context.getString(R.string.error_server);
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data"))
                        message = alcoholTest ? context.getString(R.string.msj_alcohol_test_saved) : context.getString(R.string.msj_alert_saved);
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}