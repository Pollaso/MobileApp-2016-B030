package ipn.mobileapp.model.utility;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;

public final class JsonUtils {
    private static final Gson gson = new Gson();

    public static boolean isValidJson(String json) {
        try {
            if (json != null) {
                gson.fromJson(json, Object.class);
                return true;
            }
        } catch (JsonSyntaxException e) {
            return false;
        }
        return false;
    }
}
