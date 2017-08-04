package ipn.mobileapp.Model.Components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import ipn.mobileapp.R;

public class AlertDialog implements View.OnClickListener {
    private Context context;

    public AlertDialog(Context context)
    {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.dialog_alerts);
        openDialog.setTitle(context.getString(R.string.title_dialog_alerts));
        openDialog.show();
        ImageButton dialogLocation = (ImageButton) openDialog.findViewById(R.id.alert_location);
//        ImageButton dialogCancel = (ImageButton) openDialog.findViewById(R.id.alert_cancel);
        Button dialogSendMessage = (Button) openDialog.findViewById(R.id.alert_send_message);
        dialogLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });

//        dialogCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDialog.dismiss();
//            }
//        });

        dialogSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, context.getResources().getString(R.string.msj_alert_sent), Toast.LENGTH_LONG).show();
                openDialog.dismiss();
            }
        });
    }
}
