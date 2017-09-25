package ipn.mobileapp.model.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesManager {
    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context context, String preferencesFileName) {
        sharedPreferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    public void putValue(String key, Object value, boolean clear) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (clear)
            editor.clear();

        if (value instanceof String)
            editor.putString(key, value.toString());
        else if (value instanceof Set)
            editor.putStringSet(key, (Set<String>) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Long)
            editor.putLong(key, (Long) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);

        editor.commit();
    }

    public void putValues(Map<String, Object> params, boolean clear) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (clear)
            editor.clear();
        for (Map.Entry map : params.entrySet()) {
            Object value = map.getValue();
            if (value instanceof String)
                editor.putString(map.getKey().toString(), value.toString());
            else if (value instanceof Set)
                editor.putStringSet(map.getKey().toString(), (Set<String>) value);
            else if (value instanceof Integer)
                editor.putInt(map.getKey().toString(), (Integer) value);
            else if (value instanceof Long)
                editor.putLong(map.getKey().toString(), (Long) value);
            else if (value instanceof Float)
                editor.putFloat(map.getKey().toString(), (Float) value);
            else if (value instanceof Boolean)
                editor.putBoolean(map.getKey().toString(), (Boolean) value);
        }
        editor.commit();
    }

    public Object getValue(String key, Class<?> type) {
        Object value = null;
        if (type == String.class)
            value = sharedPreferences.getString(key, null);
        else if (type == Set.class)
            value = sharedPreferences.getStringSet(key, null);
        else if (type == Integer.class)
            value = sharedPreferences.getInt(key, -1);
        else if (type == Long.class)
            value = sharedPreferences.getLong(key, -1);
        else if (type == Float.class)
            value = sharedPreferences.getFloat(key, -1);
        else if (type == Boolean.class)
            value = sharedPreferences.getBoolean(key, false);
        return value;
    }
}
