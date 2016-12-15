package crawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Anand on 10/23/2016.
 */
public class MatchInfo {
    private ArrayList<PlayerInfo> players;
    private boolean radiant_win;
    private long duration;
    private long pre_game_duration;
    private long start_time;
    private String match_id;
    private String match_seq_num;
    private int tower_status_radiant;
    private int tower_status_dire;
    private int barracks_status_radiant;
    private int barracks_status_dire;
    private int cluster;
    private int first_blood_time;
    private int lobby_type;
    private int human_players;
    private int leagueid;
    private int positive_votes;
    private int negative_votes;
    private int game_mode;
    private int flags;
    private int engine;
    private int radiant_score;
    private int dire_score;

    public MatchInfo() {
    }

    public MatchInfo(JSONObject matchInfoJSONObject) {
        try {
            if (matchInfoJSONObject.has("match_id"))
                this.match_id = matchInfoJSONObject.getString("match_id");
            if (matchInfoJSONObject.has("match_seq_num"))
                this.match_seq_num = matchInfoJSONObject.getString("match_seq_num");
            if (matchInfoJSONObject.has("start_time"))
                this.start_time = matchInfoJSONObject.getLong("start_time");
            if (matchInfoJSONObject.has("lobby_type"))
                this.lobby_type = matchInfoJSONObject.getInt("lobby_type");

            // Only available when method was GetMatchDetails
            if (matchInfoJSONObject.has("duration"))
                this.duration = matchInfoJSONObject.getLong("duration");
            if (matchInfoJSONObject.has("pre_game_duration"))
                this.pre_game_duration = matchInfoJSONObject.getLong("pre_game_duration");
            if (matchInfoJSONObject.has("tower_status_radiant"))
                this.tower_status_radiant = matchInfoJSONObject.getInt("tower_status_radiant");
            if (matchInfoJSONObject.has("tower_status_dire"))
                this.tower_status_dire = matchInfoJSONObject.getInt("tower_status_dire");
            if (matchInfoJSONObject.has("barracks_status_radiant"))
                this.barracks_status_radiant = matchInfoJSONObject.getInt("barracks_status_radiant");
            if (matchInfoJSONObject.has("barracks_status_dire"))
                this.barracks_status_dire = matchInfoJSONObject.getInt("barracks_status_dire");
            if (matchInfoJSONObject.has("cluster"))
                this.cluster = matchInfoJSONObject.getInt("cluster");
            if (matchInfoJSONObject.has("first_blood_time"))
                this.first_blood_time = matchInfoJSONObject.getInt("first_blood_time");
            if (matchInfoJSONObject.has("human_players"))
                this.human_players = matchInfoJSONObject.getInt("human_players");
            if (matchInfoJSONObject.has("leagueid"))
                this.leagueid = matchInfoJSONObject.getInt("leagueid");
            if (matchInfoJSONObject.has("positive_votes"))
                this.positive_votes = matchInfoJSONObject.getInt("positive_votes");
            if (matchInfoJSONObject.has("negative_votes"))
                this.negative_votes = matchInfoJSONObject.getInt("negative_votes");
            if (matchInfoJSONObject.has("game_mode"))
                this.game_mode = matchInfoJSONObject.getInt("game_mode");
            if (matchInfoJSONObject.has("flags"))
                this.flags = matchInfoJSONObject.getInt("flags");
            if (matchInfoJSONObject.has("engine"))
                this.engine = matchInfoJSONObject.getInt("engine");
            if (matchInfoJSONObject.has("radiant_score"))
                this.radiant_score = matchInfoJSONObject.getInt("radiant_score");
            if (matchInfoJSONObject.has("dire_score"))
                this.dire_score = matchInfoJSONObject.getInt("dire_score");

            if (matchInfoJSONObject.has("radiant_win"))
                this.radiant_win = matchInfoJSONObject.getBoolean("radiant_win");
            else if (matchInfoJSONObject.has("dire_win"))
                this.radiant_win = false;

            if (matchInfoJSONObject.has("players"))
                this.players = PlayerInfo.parsePlayersInfo(matchInfoJSONObject.getJSONArray("players"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static MatchInfo parseMatchInfo(String matchInfoJson) {
        try {
            JSONObject matchInfoWrapper = new JSONObject(matchInfoJson);
            JSONObject matchInfoJSONObject = matchInfoWrapper.getJSONObject("result");
            return new MatchInfo(matchInfoJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<PlayerInfo> getPlayersInfo() {
        return players;
    }

    public int getAvgMMR() {
        return Integer.MAX_VALUE;
    }

    public String getMatchId() {
        return match_id;
    }
}
