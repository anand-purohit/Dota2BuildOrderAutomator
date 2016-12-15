package buildExtractor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.Clarity;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.wire.common.proto.Demo;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Anand on 12/12/2016.
 */
public class ReplayInfoExtractor {
    private final Logger log = LoggerFactory.getLogger(ReplayInfoExtractor.class.getPackage().getClass());

    private int lastMinute = 0;

    private String matchId;
    private GameState currGameState;
    private ArrayList<GameState> currReplay;

    public static void main(String[] args) throws Exception {
        int count = 0;
        File replaysDir = new File("replays");
        if (replaysDir.isDirectory()) {
            String matchIds[] = replaysDir.list();
            if (matchIds != null) {
                for (String matchId : matchIds) {
                    String arg = matchId.substring(0, matchId.indexOf("."));
                    count++;
                    new ReplayInfoExtractor().run(arg);
                    System.out.println("--- Replay information extracted for match id - " + arg + " (Replays extracted - " + count +")");
                }
            }
        }
    }

    public void writeCurrReplay() {
        int currMinute = 0;
        for (GameState currGameState: currReplay) {
            try {
                File dir = new File("extractedReplays" + File.separator + matchId);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                PrintWriter writer = new PrintWriter(dir.getAbsolutePath() + File.separator + currMinute + ".json", "UTF-8");
                writer.write(currGameState.toString());
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            currMinute++;
        }
    }

    public GameState initNewGame() throws IOException {
        GameState newState = new GameState();
        Demo.CDemoFileInfo info = Clarity.infoForFile(getReplayPath(matchId));

        for (Demo.CGameInfo.CDotaGameInfo.CPlayerInfo playerInfo : info.getGameInfo().getDota().getPlayerInfoList()) {
            // Prepare a new HeroState object
            HeroState newHeroState = HeroState.createNewHeroState(playerInfo);
            newHeroState.setVictory(playerInfo.getGameTeam() == info.getGameInfo().getDota().getGameWinner());
            newHeroState.setTeam(playerInfo.getGameTeam());
            newState.addHeroState(playerInfo.getHeroName(), newHeroState);
        }

        return newState;
    }

    private void handleDeath(CombatLogEntry cle) {
        String targetName = cle.getTargetName();
        HeroState victim = currGameState.getHeroState(targetName);
        if (victim != null) {
            // Hero died...process further

            String attackerName = cle.getAttackerName();
            HeroState attacker = currGameState.getHeroState(attackerName);
            if (attacker != null) {
                // Hero was the killer
                // Increase Kill score
                attacker.increment("kills", 1);

                // Increase Team kill score
                attacker.increment("teamKills", 1);
                ArrayList<HeroState> enemies = currGameState.getAllies(attacker);
                for (HeroState ally: enemies) {
                    ally.increment("teamKills", 1);
                }
            }

            // Increase Death score
            victim.increment("deaths", 1);

            // Increase Team death score
            victim.increment("teamDeaths", 1);
            ArrayList<HeroState> allies = currGameState.getAllies(victim);
            for (HeroState ally: allies) {
                ally.increment("teamDeaths", 1);
            }
        }
    }

    private void handleGold(CombatLogEntry cle) {
        String targetName = cle.getTargetName();
        HeroState earner = currGameState.getHeroState(targetName);

        if (earner != null) {
            // Increase net worth
            earner.increment("netWorth", cle.getValue());

            // Increase team net worth
            earner.increment("teamNetWorth", cle.getValue());
            ArrayList<HeroState> allies = currGameState.getAllies(earner);
            for (HeroState ally : allies) {
                ally.increment("teamNetWorth", cle.getValue());
            }
        }
    }

    private void handleXP(CombatLogEntry cle) {
        String targetName = cle.getTargetName();
        HeroState earner = currGameState.getHeroState(targetName);

        if (earner != null) {
            // Increase XP
            earner.increment("xp", cle.getValue());

            // Increase team XP
            earner.increment("teamXp", cle.getValue());
            ArrayList<HeroState> allies = currGameState.getAllies(earner);
            for (HeroState ally : allies) {
                ally.increment("teamXp", cle.getValue());
            }
        }

    }

    private void handleBuildingDestruction(CombatLogEntry cle) {
        // Decrease towers left
        ArrayList<HeroState> victims = currGameState.getPlayers(cle.getTargetTeam());
        for (HeroState victim: victims) {
            victim.increment("towersLeft", -1);
        }
        // Decrease enemy towers left
        ArrayList<HeroState> attackers = currGameState.getPlayers(cle.getAttackerTeam());
        for (HeroState attacker: attackers) {
            attacker.increment("enemyTowersLeft", -1);
        }
    }

    private void handleItemPurchase(CombatLogEntry cle) {
        String targetName = cle.getTargetName();
        HeroState buyer = currGameState.getHeroState(targetName);
        // Change last purchased item
        buyer.setLastItemPurchased(cle.getValueName());
    }

    @OnCombatLogEntry
    public void onCombatLogEntry(Context ctx, CombatLogEntry cle) {
        int time = (int) (cle.getTimestamp() / 60);
        if (time > lastMinute) {
            GameState newGameState;
            lastMinute++;
            newGameState = new GameState(currGameState, time);
            currReplay.add(newGameState);
            currGameState = newGameState;
        }

        switch (cle.getType()) {
            case DOTA_COMBATLOG_DEATH:
                // Increase kills and deaths
                handleDeath(cle);
                break;
            case DOTA_COMBATLOG_GOLD:
                handleGold(cle);
                break;
            case DOTA_COMBATLOG_XP:
                handleXP(cle);
                break;
            case DOTA_COMBATLOG_TEAM_BUILDING_KILL:
                handleBuildingDestruction(cle);
                break;
            case DOTA_COMBATLOG_PURCHASE:
                handleItemPurchase(cle);
                break;
        }
    }

//    @OnMessage
//    public void onMessage(Context ctx, GeneratedMessage msg) {
//        if (msg instanceof S2DotaMatchMetadata.CDOTAMatchMetadataFile) {
//            // tODO: Fetch ability upgrade times
//            // TODO: Update the game state to reflect the ability upgrade times

//            // TODO: Can't do anything here for abilities coz there's nothing linking heroes and abilities in the replay!
//            S2DotaMatchMetadata.CDOTAMatchMetadataFile metadata = (S2DotaMatchMetadata.CDOTAMatchMetadataFile) msg;
//            for (S2DotaMatchMetadata.CDOTAMatchMetadata.Team currTeam: metadata.getMetadata().getTeamsList()) {
//                if (currTeam.getPlayersCount() > 0) {
//                    for (S2DotaMatchMetadata.CDOTAMatchMetadata.Team.Player currPlayer: currTeam.getPlayersList()) {
//                        List<Integer> upgrades = currPlayer.getAbilityUpgradesList();
//                        List<Integer> lvlUpTimes = currPlayer.getLevelUpTimesList();
//                        GameState startGameState = currReplay.get(0);
////                        HeroState heroState = startGameState.getHeroState()
////                        for (int i = 0; i < )
//                    }
//                }
//            }
//        }
//    }

    public String getReplayPath(String matchId) {
        return "replays" + File.separator + matchId + ".dem";
    }

    private String compileName(String attackerName, boolean isIllusion) {
        return attackerName != null ? attackerName + (isIllusion ? " (illusion)" : "") : "UNKNOWN";
    }

    public String getAttackerNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getAttackerName(), cle.isAttackerIllusion());
    }

    public String getTargetNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getTargetName(), cle.isTargetIllusion());
    }

    public void run(String arg) throws Exception {
        long tStart = System.currentTimeMillis();

        currReplay = new ArrayList<>();
        matchId = arg;
        currGameState = initNewGame();
        lastMinute = 0;

        new SimpleRunner(new MappedFileSource(getReplayPath(arg))).runWith(this);

        writeCurrReplay();

        long tMatch = System.currentTimeMillis() - tStart;
        log.info("total time taken: {}s", (tMatch) / 1000.0);
    }
}
