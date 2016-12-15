package crawler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anand on 12/3/2016.
 */
public class JSONWrapper {
    JSONObject json;

    public Object getObjectValue(String key) throws JSONException {
        if (json.has(key)) {
            return json.get(key);
        }

        return null;
    }

    public JSONArray getArrayValue(String key) throws JSONException {
        if (json.has(key)) {
            return json.getJSONArray(key);
        }

        return null;
    }
}
