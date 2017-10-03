package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.content.DialogInterface;
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

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Alert;
import ipn.mobileapp.presenter.dialogs.SMSAlertDialog;

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
        imgBtnAlertPreview.setOnClickListener(new SMSAlertDialog(context));

        TextView tvAlertUserName = (TextView) convertView.findViewById(R.id.tv_alert_user_name);
        tvAlertUserName.setText(alert.getSenderName());

        TextView tvAlertDate = (TextView) convertView.findViewById(R.id.tv_alert_date);
        tvAlertDate.setText(alert.getDateSent().toString());

        TextView tvAlertBac = (TextView) convertView.findViewById(R.id.tv_alert_bac);
        tvAlertBac.setText(Double.toString(alert.getAlcoholicState()));

        return convertView;
    }
}

