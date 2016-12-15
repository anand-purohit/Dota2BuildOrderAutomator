package crawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * Created by Anand on 12/3/2016.
 */
public class ODotaMatchInfo extends JSONWrapper{
    public ODotaMatchInfo(JSONObject jsonObject) {
        json = jsonObject;
    }

    public static ODotaMatchInfo parseMatchInfo(String matchInfoJsonStr) {
        JSONObject matchInfoJson = null;
        try {
            matchInfoJson = new JSONObject(matchInfoJsonStr);
            return new ODotaMatchInfo(matchInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeToFile(String filePath) {
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.write(json.toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
