package dataCollection;

import data.Const;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Anand on 9/24/2016.
 */
@SuppressWarnings("Duplicates")
public class DataRequester {
    //    String addr = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/";
//    String keySuffix = "key=" + Const.STEAM_WEB_API_KEY;
//    String formatSuffix = "format=" + "json";
    String baseURL;
    String interfaceName;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    String method;
    String version;
    String params;
    HashMap<String, String> paramsMap;

    public DataRequester() {
        this.baseURL = "https://api.steampowered.com";
        this.interfaceName = "IDOTA2Match_570";
        this.method = "GetMatchHistory";
        this.version = "v1";
        this.paramsMap = new HashMap<>();
        this.paramsMap.put("key", Const.STEAM_WEB_API_KEY);
        this.paramsMap.put("format", Const.Formats.JSON);
    }

    public static void main(String[] args) {
        DataRequester dataRequester = new DataRequester();
//        dataRequester.requestData();

//        dataRequester.setAccountID(Const.AccountIDs.W33_32);
//        dataRequester.setAccountID("185277836");
//        dataRequester.setAccountID("367397392");
        dataRequester.setAccountID("76561198330821400");
        dataRequester.setMatchesRequested(10);
        dataRequester.setMinPlayers(10);
//        dataRequester.setMatchId("2569415435");
//        dataRequester.setSkill(3);
//        dataRequester.setGameMode(1);
//        dataRequester.requestData();

        DataRequester detailsRequester = new DataRequester();
        detailsRequester.method = "GetMatchDetails";
//        detailsRequester.setMatchId("2569415435");
        detailsRequester.setMatchId("2671817672");
        detailsRequester.requestData();
    }

    // This method works. Check testOut, for an idea about the kind of output received as a response
//    public void requestData() {
//        String url = addr + "?" + formatSuffix + "&" + keySuffix;
    // I can only send a limited number of requests to the valve server everyday.
    // Best not to screw with that limit.

//        try {
//            InputStream response = new URL(url).openStream();
//            Scanner sc = new Scanner(response);
//            String responseBody = sc.useDelimiter("\\A").next();
//            System.out.println(responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public String requestData() {
        String url = getURL();
//        System.out.println(url);
        try {
            InputStream response = new URL(url).openStream();
            Scanner sc = new Scanner(response);
            String responseBody = sc.useDelimiter("\\A").next();
//            System.out.println(responseBody);
            return responseBody;
            // TODO: Check if TI6 match details are getting fetched
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMatchId(String matchId) {
        this.paramsMap.put("match_id", matchId);
    }

    public void setAccountID(String accountID) {
        this.paramsMap.put("account_id", accountID);
    }

    public void setMatchesRequested(int matchCount) {
        this.paramsMap.put("matches_requested", matchCount + "");
    }

    public void setMinPlayers(int minPlayerCount) {
        assert (minPlayerCount <= 10);
        this.paramsMap.put("min_players", minPlayerCount + "");
    }

    public void setSkill(int skill) {
        assert(skill > 0);
        assert(skill <= 3);
        this.paramsMap.put("skill", skill + "");
    }

    public void setGameMode(int gameMode) {
        assert (gameMode > 0);
        assert (gameMode <= 16);
        this.paramsMap.put("game_mode", gameMode + "");
    }

    public void setHeroId(int heroId) {
        // TODO: Also keep a method for setting hero using the hero's name
        // TODO: Fetch hero and item information
        this.paramsMap.put("hero_id", heroId + "");
    }

    public String getURL() {
        params = getParamsStr();

        return baseURL + "/" + interfaceName + "/" + method + "/" + version + "/" + "?" + params;
    }

    private String getParamsStr() {
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
}
