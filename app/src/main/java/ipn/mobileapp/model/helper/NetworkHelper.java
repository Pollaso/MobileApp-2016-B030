package ipn.mobileapp.model.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    private Context context;

    public NetworkHelper(Context context){
        this.context = context;
    }

    public boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean hasWifiConnection() {
        boolean wifiConnection = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo.isConnected())
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                wifiConnection = true;

        return wifiConnection;
    }
}
