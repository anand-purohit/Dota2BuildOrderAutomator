package dataCollection;

import crawler.ODotaMatchInfo;
import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Anand on 12/3/2016.
 */
@SuppressWarnings("Duplicates")
public class ODotaDataRequester {
    String baseURL;
    String method;
    String pathParams;
    HashMap<String, String> paramsMap;

    public ODotaDataRequester() {
        this.baseURL = "https://api.opendota.com/api";
        this.method = "";
        this.pathParams = "";
        this.paramsMap = new HashMap<>();
    }

    public static void main(String[] args) {
        ODotaDataRequester oDotaDataRequester = new ODotaDataRequester();
        oDotaDataRequester.setMethod("matches");
        oDotaDataRequester.setPathParams("2816816242");
        String response = oDotaDataRequester.requestData();

//        System.out.printf("--------------");
//
//        try {
//            String replayLink = "http://replay112.valve.net/570/2798856666_1740102935.dem.bz2";
//            URLConnection urlConnection = new URL(replayLink).openConnection();
//            InputStream inputStream = urlConnection.getInputStream();
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//            System.out.println(bufferedInputStream.read());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String requestData() {
        String urlStr = getURL();
        System.out.println(urlStr);
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(urlStr);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
            InputStream response = httpsURLConnection.getInputStream();
            Scanner sc = new Scanner(response);
            String responseBody = sc.useDelimiter("\\A").next();
//            System.out.println(responseBody);
            return responseBody;
        } catch (MalformedURLException e) {
            System.out.printf("Malformed URL");
            e.printStackTrace();
        } catch (IOException e) {
            if (httpsURLConnection != null) {
                InputStream errorInputStream = httpsURLConnection.getErrorStream();
                if (errorInputStream != null) {
                    Scanner sc = new Scanner(errorInputStream);
                    System.out.println(sc.useDelimiter("\\A").next());
                } else {
                    System.out.println("Error input stream is null for match with URL - " + urlStr);
                }
            }
            e.printStackTrace();
        }

        return null;
    }

    private String getParamsMapStr() {
        StringBuilder sb = new StringBuilder();
        int paramCount = 0;
        for (String key : paramsMap.keySet()) {
            sb.append(key).append("=").append(paramsMap.get(key));

            if (paramCount != paramsMap.size() - 1) {
                sb.append("&");
            }
            paramCount++;
        }

        return sb.toString();
    }

    public String getURL() {
        String paramsMapStr = getParamsMapStr();

        return baseURL + "/" + method + "/" + pathParams + (paramsMapStr.trim().isEmpty() ? "" : "?" + paramsMapStr);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathParams() {
        return pathParams;
    }

    public void setPathParams(String pathParams) {
        this.pathParams = pathParams;
    }

    public void addToParamsMap(String key, String value) {
        paramsMap.put(key, value);
    }

    public String getFromParamsMap(String key) {
        return paramsMap.get(key);
    }

    public boolean downloadReplay(String replayURL, String filePath) {
        try {
            URLConnection urlConnection =  new URL(replayURL).openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36");
            urlConnection.setRequestProperty("Cookie","__cfduid=d7b67e3c0867f07834bd487ce2caa967c1480835644; cf_clearance=7f78b5ad63c003d3056936f5733bb77160bc4509-1480835652-1800");
            InputStream inputStream = urlConnection.getInputStream();

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }
            System.out.println("Replay downloaded to - " + filePath);

            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Unable to download the replay with link - " + replayURL + " to the path - " + filePath);
        return false;
    }

    public boolean downloadMatchInfoAndReplay(String matchInfoFilePath, String replayFilePath) throws JSONException {
        this.method = "matches";
        String matchInfo = requestData();

        ODotaMatchInfo oDotaMatchInfo = ODotaMatchInfo.parseMatchInfo(matchInfo);

        if (oDotaMatchInfo == null) {
            System.out.println("Unable to download match info");
            return false;
        }

        oDotaMatchInfo.writeToFile(matchInfoFilePath);

        String replayURL = (String) oDotaMatchInfo.getObjectValue("replay_url");
        if (replayURL == null) {
            System.out.println("Replay URL not found. Skipping replay download...");
            return false;
        } else {
            return downloadReplay(replayURL, replayFilePath);
        }
    }

}
