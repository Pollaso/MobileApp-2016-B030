package ipn.mobileapp.presenter.dialogs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.model.pojo.Coordinate;
import ipn.mobileapp.model.pojo.User;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertAlcoholDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private DialogInterface.OnDismissListener dismissListener;

    private ImageButton imgBtnLocation;
    private Button btnSendMessage;
    private Button btnCancel;
    private EditText etMessage;
    private TextView tvAlertUserName;
    private TextView tvAlertDate;
    private TextView tvAlertBac;

    private Alert alert;

    private String sms;

    public AlertAlcoholDialog(Context context, Alert alert, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        this.alert = alert;
        this.dismissListener = dismissListener;
    }

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
        if (alert.getAlertState() == 0)
            updateState(1);

    }

    private void createDialog() {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_alert)
                .setTitle(context.getString(R.string.title_dialog_alerts))
                .setOnDismissListener(dismissListener)
                .create();
        dialog.show();
    }

    private void getComponents() {
        imgBtnLocation = (ImageButton) dialog.findViewById(R.id.img_btn_location);
        btnSendMessage = (Button) dialog.findViewById(R.id.btn_send_message);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        etMessage = (EditText) dialog.findViewById(R.id.et_message);
        tvAlertUserName = (TextView) dialog.findViewById(R.id.tv_alert_user_name);
        tvAlertDate = (TextView) dialog.findViewById(R.id.tv_alert_date);
        tvAlertBac = (TextView) dialog.findViewById(R.id.tv_alert_bac);
    }

    private void setComponentAttributes() {
        btnSendMessage.setOnClickListener(sendMessage);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        imgBtnLocation.setOnClickListener(openLocation);

        tvAlertUserName.setText(alert.getSenderName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alert.getDateSent().getTime());
        Formatter formatter = new Formatter(new Locale("es", "ES"));
        formatter.format("%tB %td %tY", calendar, calendar, calendar);
        tvAlertDate.setText(formatter.toString());

        float bac = (float) (alert.getAlcoholicState() / 2600.00);
        tvAlertBac.setText(String.format("%.4f", bac) + "% de alcohol en sangre");

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etMessage};
        etMessage.addTextChangedListener(new TextValidator(etMessage) {
            @Override
            public void validate(TextView textView, String text) {
                sms = text;
                btnSendMessage.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private void updateState(int state) {
        alert.setAlertState(state);
        Map<String, String> params = new HashMap<>();
        params.put("alert", alert.toString());

        OkHttpServletRequest request = new OkHttpServletRequest(context);
        Request builtRequest = request.buildRequest(Servlets.ALERT, RequestType.PUT, params);
        OkHttpClient client = request.buildClient();
        client.newCall(builtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonResponse = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        String message = context.getString(R.string.error_server);
                        if (jsonResponse != null && JsonUtils.isValidJson(jsonResponse)) {
                            JsonObject json = new JsonParser().parse(jsonResponse).getAsJsonObject();
                            if (json.has("data"))
                                message = context.getString(R.string.msj_alert_state_changed);
                            else if (json.has("warnings"))
                                message = context.getString(R.string.warning_alert_state_changed);
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private ImageButton.OnClickListener openLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Coordinate coordinate = alert.getCoordinate();
            Uri gmmIntentUri = Uri.parse("geo:" + coordinate.getLatitude() + "," + coordinate.getLongitude() + "?q=" + coordinate.getLatitude() + "," + coordinate.getLongitude() + " (Localización de " + alert.getSenderName() + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null)
                context.startActivity(mapIntent);
        }
    };

    private Button.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

            try {
                ArrayList<String> parts = smsManager.divideMessage(sms);
                ArrayList<PendingIntent> sentList = new ArrayList<>();
                for (int i = 0; i < parts.size(); i++)
                    sentList.add(sentPI);

                smsManager.sendMultipartTextMessage(alert.getSenderPhone(), null, parts, sentList, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(context, context.getResources().getString(R.string.msj_sms_sent), Toast.LENGTH_LONG).show();

            if (alert.getAlertState() != 2)
                updateState(2);

            dialog.dismiss();
        }
    };
}
