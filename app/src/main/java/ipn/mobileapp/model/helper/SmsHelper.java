package ipn.mobileapp.model.helper;

import android.content.Context;
import ipn.mobileapp.R;
import ipn.mobileapp.model.pojo.Coordinate;

public class SmsHelper {
    private Context context;

    public SmsHelper(Context context) {
        this.context = context;
    }

    public String generateSmsBody(int ppm, Coordinate coordinate, String name) {
        String googleMapsUrl = "https://maps.google.com/?q=" + coordinate.getLatitude() + "," + coordinate.getLongitude();
        String body = "DACBA:\n" + name + " tiene un porcentaje de alcohol en sangre de ";

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
