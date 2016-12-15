package buildExtractor;

import org.json.JSONException;
import org.json.JSONObject;
import skadistats.clarity.wire.common.proto.Demo;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Anand on 12/12/2016.
 */
public class HeroState {
    JSONObject heroState;
    boolean victory;
    String lastItemPurchased;
    String lastAbilityUpgraded;
    int team;

    static HashMap<String, Integer> maxValMap = new HashMap<>();

    public HeroState() {
        heroState = new JSONObject();
        if (maxValMap.size() == 0) {
            maxValMap.put("netWorth", 100000);
            maxValMap.put("teamNetWorth", 100000);
            maxValMap.put("xp", 32400);
            maxValMap.put("teamXp", 32400 * 5);
            maxValMap.put("kills", 1000);
            maxValMap.put("teamKills", 1000);
            maxValMap.put("deaths", 1000);
            maxValMap.put("teamDeaths", 1000);
            maxValMap.put("towersLeft", 33);
            maxValMap.put("enemyTowersLeft", 33);
        }
    }

    public HeroState(HeroState heroState) {
        this();

        Iterator itr = heroState.getHeroState().keys();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            try {
                this.heroState.put(key, heroState.getHeroState().get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.victory = heroState.isVictory();
        this.team = heroState.getTeam();
        this.lastAbilityUpgraded = "";
        this.lastItemPurchased = "";
    }

    public void set(String property, int value) {
        try {
            heroState.put(property, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void increment(String property, int value) {
        try {
            heroState.put(property, (int) heroState.get(property) + value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Object get(String property) {
        try {
            return heroState.get(property);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public float getNormalized(String property) {
        int val = (int) get(property);
        int maxVal = maxValMap.get(property);
        return ((float) val) / maxVal;
    }

    public JSONObject getHeroState() {
        return heroState;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public String getLastItemPurchased() {
        return lastItemPurchased;
    }

    public void setLastItemPurchased(String lastItemPurchased) {
        this.lastItemPurchased = lastItemPurchased;
    }

    public String getLastAbilityUpgraded() {
        return lastAbilityUpgraded;
    }

    public void setLastAbilityUpgraded(String lastAbilityUpgraded) {
        this.lastAbilityUpgraded = lastAbilityUpgraded;
    }

    @Override
    public String toString() {
        JSONObject fullHeroState = getFullJSON(false);
        if (fullHeroState == null) {
            return null;
        }

        return fullHeroState.toString();
    }

    public JSONObject getFullJSON(boolean normalize) {
        JSONObject fullHeroState = new JSONObject();
        try {
            fullHeroState.put("state", normalize ? normalize() : heroState);
            fullHeroState.put("victory", victory);
            fullHeroState.put("lastItemPurchased", ItemFinder.getItemClassified(lastItemPurchased));
            fullHeroState.put("lastAbilityUpgraded", lastAbilityUpgraded);

            return fullHeroState;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject normalize() {
        JSONObject normalizedHeroState = new JSONObject();
        try {
            normalizedHeroState.put("netWorth", getNormalized("netWorth"));
            normalizedHeroState.put("teamNetWorth", getNormalized("teamNetWorth"));
            normalizedHeroState.put("xp", getNormalized("xp"));
            normalizedHeroState.put("teamXp", getNormalized("teamXp"));
            normalizedHeroState.put("kills", getNormalized("kills"));
            normalizedHeroState.put("teamKills", getNormalized("teamKills"));
            normalizedHeroState.put("deaths", getNormalized("deaths"));
            normalizedHeroState.put("teamDeaths", getNormalized("teamDeaths"));
            normalizedHeroState.put("towersLeft", getNormalized("towersLeft"));
            normalizedHeroState.put("enemyTowersLeft", getNormalized("enemyTowersLeft"));

            return normalizedHeroState;
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return null;
    }

    public boolean isAlly(HeroState other) {
        return this.team == other.team;
    }

    public static HeroState createNewHeroState(Demo.CGameInfo.CDotaGameInfo.CPlayerInfo playerInfo) {
        HeroState heroState = new HeroState();
        heroState.set("netWorth", 0);
        heroState.set("teamNetWorth", 0);
        heroState.set("xp", 0);
        heroState.set("teamXp", 0);
        heroState.set("kills", 0);
        heroState.set("teamKills", 0);
        heroState.set("deaths", 0);
        heroState.set("teamDeaths", 0);
        heroState.set("towersLeft", 33);
        heroState.set("enemyTowersLeft", 33);

        return heroState;
    }
}
