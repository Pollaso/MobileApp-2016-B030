package ipn.mobileapp.model.helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.AlcoholTest;
import ipn.mobileapp.model.pojo.Coordinate;
import ipn.mobileapp.model.pojo.User;

public class AlcoholTestHelper {
    private Context context;
    private User user;

    public AlcoholTestHelper(Context context, User user) {
        this.user = user;
        this.context = context;
    }

    public void generateAlcoholTest(int ppm){
        AlcoholTest alcoholTest =  new AlcoholTest();
        alcoholTest.setAlcoholicState(ppm);
        Date currentTime = Calendar.getInstance().getTime();
        alcoholTest.setOcurrence((java.sql.Date) currentTime);
    }

    public void sendSms(int ppm, Coordinate coordinate){
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        String sms = generateSmsBody(ppm, coordinate);
        try {
            ArrayList<String> parts = smsManager.divideMessage(sms);
            ArrayList<PendingIntent> sentList = new ArrayList<>();
            for (int i = 0; i < parts.size(); i++)
                sentList.add(sentPI);

            smsManager.sendMultipartTextMessage("+525519718397", null, parts, sentList, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(context, context.getResources().getString(R.string.msj_sms_sent), Toast.LENGTH_LONG).show();
    }

    public String generateSmsBody(int ppm, Coordinate coordinate) {
        String googleMapsUrl = "https://maps.google.com/?q=" + coordinate.getLatitude() + "," + coordinate.getLongitude();
        String body = "DACBA:\n" + user.getName() + " tiene un porcentaje de alcohol en sangre de ";

        float bac = (float) (ppm / 2600.00);
        body += String.format("%.4f", bac);

        if (bac > 0.0 && bac < 0.05)
            body += ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[0];
        else if (bac >= 0.05 && bac < 0.15)
            body += ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[1];
        else if (bac >= 0.15 && bac < 0.25)
            body += ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[2];
        else if (bac >= 0.25 && bac < 0.40)
            body += ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[3];
        else
            body += ":\n" + context.getResources().getStringArray(R.array.alcohol_effects)[4];

        body += "\nEl usuario se encuentra ubicado en:\n" + googleMapsUrl;
        return body;
    }
}
