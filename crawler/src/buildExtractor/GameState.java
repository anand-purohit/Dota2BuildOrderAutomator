package buildExtractor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anand on 12/12/2016.
 */
public class GameState {
    HashMap<String, HeroState> stateMap;
    int gameTime;

    public GameState() {
        stateMap = new HashMap<>();
        gameTime = 0;
    }

    public GameState(int gameTime) {
        this();
        this.gameTime = gameTime;
    }

    public GameState(GameState stateMap, int gameTime) {
        this(gameTime);
        for (String heroName: stateMap.stateMap.keySet()) {
            this.stateMap.put(heroName, new HeroState(stateMap.stateMap.get(heroName)));
        }
    }

    public void addHeroState(String heroName, HeroState heroState) {
        this.stateMap.put(heroName, heroState);
    }

    public ArrayList<HeroState> getAllies(HeroState heroState) {
        ArrayList<HeroState> allies = new ArrayList<>();
        for (String heroName: stateMap.keySet()) {
            HeroState hero = stateMap.get(heroName);
            if (hero != heroState) {
                if (heroState.isAlly(hero)) {
                    allies.add(hero);
                }
            }
        }

        return allies;
    }

    public ArrayList<HeroState> getEnemies(HeroState heroState) {
        ArrayList<HeroState> enemies = new ArrayList<>();
        for (String heroName: stateMap.keySet()) {
            HeroState hero = stateMap.get(heroName);
            if (hero != heroState) {
                if (!heroState.isAlly(hero)) {
                    enemies.add(hero);
                }
            }
        }

        return enemies;
    }

    public ArrayList<HeroState> getPlayers(int team) {
        ArrayList<HeroState> players = new ArrayList<>();
        for (String heroName: stateMap.keySet()) {
            HeroState hero = stateMap.get(heroName);
            if (hero.getTeam() == team) {
                players.add(hero);
            }
        }

        return players;
    }

    public HeroState getHeroState(String heroName) {
        return stateMap.get(heroName);
    }

    public String toString() {
        JSONObject gameState = new JSONObject();
        try {
            gameState.put("gameTime", gameTime);

            JSONObject heroStateJSON = new JSONObject();
            for (String heroName: stateMap.keySet()) {
                heroStateJSON.put(heroName, stateMap.get(heroName).getFullJSON(true));
            }

            gameState.put("state", heroStateJSON);

            return gameState.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
