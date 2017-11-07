package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.presenter.dialogs.AlertAlcoholDialog;

public class AlertAdapter extends ArrayAdapter<Alert> {
    private Context context;
    private int resource;
    private DialogInterface.OnDismissListener dismissListener;

    public AlertAdapter(@NonNull Context context, @NonNull @LayoutRes int resource, @NonNull ArrayList<Alert> alerts, DialogInterface.OnDismissListener dismissListener) {
        super(context, resource, alerts);
        this.context = context;
        this.resource = resource;
        this.dismissListener = dismissListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Alert alert = getItem(position);

        if (convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(context);
            convertView = inflator.inflate(resource, parent, false);
        }

        ImageButton imgBtnAlertPreview = (ImageButton) convertView.findViewById(R.id.img_btn_alert_preview);
        Drawable imgBtnIcon;
        switch (alert.getAlertState()) {
            case Alert.NEW:
                imgBtnIcon = context.getResources().getDrawable(R.drawable.ic_alert_new);
                break;
            case Alert.PENDING:
                imgBtnIcon = context.getResources().getDrawable(R.drawable.ic_alert_pending);
                break;
            case Alert.ANSWERED:
                imgBtnIcon = context.getResources().getDrawable(R.drawable.ic_alert_answered);
                break;
            default:
                imgBtnIcon = null;
                break;
        }
        if (imgBtnIcon != null)
            imgBtnAlertPreview.setBackground(imgBtnIcon);
        imgBtnAlertPreview.setOnClickListener(new AlertAlcoholDialog(context, alert, dismissListener));

        TextView tvAlertUserName = (TextView) convertView.findViewById(R.id.tv_alert_user_name);
        tvAlertUserName.setText(alert.getSenderName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alert.getDateSent().getTime());
        Formatter formatter = new Formatter(new Locale("es", "ES"));
        formatter.format("%tB %td %tY", calendar, calendar, calendar);
        TextView tvAlertDate = (TextView) convertView.findViewById(R.id.tv_alert_date);
        tvAlertDate.setText(formatter.toString());

        TextView tvAlertBac = (TextView) convertView.findViewById(R.id.tv_alert_bac);
        float bac = (float) (alert.getAlcoholicState() / 2600.00);
        tvAlertBac.setText(String.format("%.4f", bac) + "% de alcohol en sangre");

        return convertView;
    }
}

