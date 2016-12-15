package crawler;

import dataCollection.DataRequester;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Anand on 10/23/2016.
 */
public class PlayerInfo {
    private String account_id;
    private int player_slot;
    private int hero_id;
    private int kills;
    private int deaths;
    private int assists;
    private int leaver_status;
    private int last_hits;
    private int denies;
    private int gold_per_min;
    private int xp_per_min;
    private int level;
    private int hero_damage;
    private int tower_damage;
    private int hero_healing;
    private int gold;
    private int gold_spent;
    private int scaled_hero_damage;
    private int scaled_tower_damage;
    private int scaled_hero_healing;

    public PlayerInfo(JSONObject playerInfoJSON) {
        try {
            if (playerInfoJSON.has("account_id"))
                this.account_id = playerInfoJSON.getString("account_id");
            if (playerInfoJSON.has("player_slot"))
                this.player_slot = playerInfoJSON.getInt("player_slot");
            if (playerInfoJSON.has("hero_id"))
                this.hero_id = playerInfoJSON.getInt("hero_id");
            if (playerInfoJSON.has("kills"))
                this.kills = playerInfoJSON.getInt("kills");
            if (playerInfoJSON.has("deaths"))
                this.deaths = playerInfoJSON.getInt("deaths");
            if (playerInfoJSON.has("assists"))
                this.assists = playerInfoJSON.getInt("assists");
            if (playerInfoJSON.has("leaver_status"))
                this.leaver_status = playerInfoJSON.getInt("leaver_status");
            if (playerInfoJSON.has("last_hits"))
                this.last_hits = playerInfoJSON.getInt("last_hits");
            if (playerInfoJSON.has("denies"))
                this.denies = playerInfoJSON.getInt("denies");
            if (playerInfoJSON.has("gold_per_min"))
                this.gold_per_min = playerInfoJSON.getInt("gold_per_min");
            if (playerInfoJSON.has("xp_per_min"))
                this.xp_per_min = playerInfoJSON.getInt("xp_per_min");
            if (playerInfoJSON.has("level"))
                this.level = playerInfoJSON.getInt("level");
            if (playerInfoJSON.has("hero_damage"))
                this.hero_damage = playerInfoJSON.getInt("hero_damage");
            if (playerInfoJSON.has("tower_damage"))
                this.tower_damage = playerInfoJSON.getInt("tower_damage");
            if (playerInfoJSON.has("hero_healing"))
                this.hero_healing = playerInfoJSON.getInt("hero_healing");
            if (playerInfoJSON.has("gold"))
                this.gold = playerInfoJSON.getInt("gold");
            if (playerInfoJSON.has("gold_spent"))
                this.gold_spent = playerInfoJSON.getInt("gold_spent");
            if (playerInfoJSON.has("scaled_hero_damage"))
                this.scaled_hero_damage = playerInfoJSON.getInt("scaled_hero_damage");
            if (playerInfoJSON.has("scaled_tower_damage"))
                this.scaled_tower_damage = playerInfoJSON.getInt("scaled_tower_damage");
            if (playerInfoJSON.has("scaled_hero_healing"))
                this.scaled_hero_healing = playerInfoJSON.getInt("scaled_hero_healing");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Currently not parsing ability and item info here

    public static ArrayList<PlayerInfo> parsePlayersInfo(JSONArray playersInfoJSON) {
        ArrayList<PlayerInfo> playersInfo = new ArrayList<>();
        for (int i = 0; i < playersInfoJSON.length(); i++) {
            try {
                PlayerInfo playerInfo = parsePlayerInfo(playersInfoJSON.getJSONObject(i));
                playersInfo.add(playerInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return playersInfo;
    }

    public static PlayerInfo parsePlayerInfo(JSONObject playerInfoJSON) {
        return new PlayerInfo(playerInfoJSON);
    }

    public ArrayList<MatchInfo> getPastMatches() {
        DataRequester detailsRequester = new DataRequester();
        detailsRequester.setMethod("GetMatchHistory");
        detailsRequester.setAccountID(account_id);
        detailsRequester.setMinPlayers(10);
        detailsRequester.setMatchesRequested(10);
        String response = detailsRequester.requestData();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resultObj = jsonObject.getJSONObject("result");
            int status = resultObj.getInt("status");
            if (status == 1) {
                JSONArray matches = resultObj.getJSONArray("matches");

                ArrayList<MatchInfo> pastMatches = new ArrayList<>();
                for (int i = 0; i < matches.length(); i++) {
                    MatchInfo matchInfo = new MatchInfo((JSONObject) matches.get(i));
                    pastMatches.add(matchInfo);
                }

                return pastMatches;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
