package ipn.mobileapp.presenter.dialogs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.model.pojo.Coordinate;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;

public class SMSAlertDialog implements View.OnClickListener {
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

    public SMSAlertDialog(Context context, Alert alert, DialogInterface.OnDismissListener dismissListener) {
        this.context = context;
        this.alert = alert;
        this.dismissListener = dismissListener;
    }

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
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
        tvAlertDate.setText(alert.getDateSent().toString());
        tvAlertBac.setText(Double.toString(alert.getAlcoholicState()));

        final Validator validator = new Validator(context);
        final TextView[] fields = new TextView[]{etMessage};
        etMessage.addTextChangedListener(new TextValidator(etMessage) {
            @Override
            public void validate(TextView textView, String text) {
                btnSendMessage.setEnabled(validator.validateFields(fields));
            }
        });
    }

    private String getMessageBody(String googleMapsUrl) {
        String body = "DACBA:\n" + alert.getSenderName() + " tiene un porcentaje de alcohol en sangre de " + alert.getAlcoholicState() + ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[1];

        body += "\nEl usuario se encuentra ubicado en:\n" + googleMapsUrl;
        return body;
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
            Coordinate coordinate = alert.getCoordinate();
            String urlGoogleMaps = "https://maps.google.com/?q=" + coordinate.getLatitude() + "," + coordinate.getLongitude();
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

            String sms = getMessageBody(urlGoogleMaps);
            try {
                smsManager.sendTextMessage("+525519718397", null, sms, sentPI, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(context, context.getResources().getString(R.string.msj_alert_sent), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    };
}
