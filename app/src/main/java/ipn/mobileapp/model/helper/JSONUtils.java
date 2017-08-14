package ipn.mobileapp.model.helper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public final class JSONUtils {
    private static final Gson gson = new Gson();

    private JSONUtils() {
    }

    public static boolean isValidJSON(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
