package crawler;

import dataCollection.DataRequester;
import dataCollection.ODotaDataRequester;
import org.json.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Anand on 10/23/2016.
 */
public class Crawler {
    private String seedFilePath;
    private int thresholdMMR;
    private int dataSize;
    private String matchIdFile;
    private String replayDir;

    private Queue<String> matchIds;
    private HashSet<String> seedMatchIds;
    private HashSet<String> trainingMatchIds;

    public Crawler() {
        matchIds = new LinkedList<>();
        trainingMatchIds = new HashSet<>();
    }

    public HashSet<String> readSeedFile(String filePath) {
        HashSet<String> matchIds = new HashSet<>();
        Path path = Paths.get(filePath);
        try(Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                matchIds.add(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchIds;
    }

    public MatchInfo getMatchInfo(String matchId) {
        DataRequester detailsRequester = new DataRequester();
        detailsRequester.setMethod("GetMatchDetails");
        detailsRequester.setMatchId(matchId);
        String response = detailsRequester.requestData();

        return MatchInfo.parseMatchInfo(response);
    }

    private void crawlFurther(MatchInfo matchInfo) {
        ArrayList<PlayerInfo> playersInfo = matchInfo.getPlayersInfo();
        MatchInfo[][] playersMatches = new MatchInfo[10][10];
        int playerNum = 0;

        for (PlayerInfo currPlayerInfo: playersInfo) {
            ArrayList<MatchInfo> pastMatches = currPlayerInfo.getPastMatches();
            if (pastMatches != null) {
                int i = 0;
                for (MatchInfo pastMatchInfo : pastMatches) {
                    playersMatches[playerNum][i++] = pastMatchInfo;
                }
                playerNum++;
            }
        }

        // Add all games into matchIds (column-wise)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < playerNum; j++) {
                // TODO: This path is a bit circuitous (maybe make matchIds store MatchInfo instead?)
                if (playersMatches[j][i] != null) {
                    matchIds.offer(playersMatches[j][i].getMatchId());
                }
            }
        }
    }

    private void prepareInitialData() {
        seedMatchIds = readSeedFile(seedFilePath);

        for (String seedMatch: seedMatchIds) {
            matchIds.offer(seedMatch);
        }
    }

    private void prepareDataSet() {
        while (trainingMatchIds.size() < dataSize) {
            if (matchIds.size() == 0) {
                System.out.println("******* Queue empty, need additional players / games to seed");
                return;
            }

            String matchId = matchIds.poll();
            if (!trainingMatchIds.contains(matchId)) {
                boolean dlReplay = true;
                MatchInfo matchInfo = getMatchInfo(matchId);

                if (!seedMatchIds.contains(matchId)) {
                    // If match is not from the seed, check avg MMR for the game
                    dlReplay = matchInfo.getAvgMMR() >= thresholdMMR;
                }

                if (dlReplay) {
//                    downloadReplay(matchInfo);
                    crawlFurther(matchInfo);
                    trainingMatchIds.add(matchId);
                    System.out.println("----- Matches crawled - " + trainingMatchIds.size());
                }
            }
        }
    }


    private void writeMatchIds(String file, HashSet<String> matchIds) {
        try {
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            for (String matchId: matchIds) {
                System.out.println(matchId);
                writer.println(matchId);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public boolean downloadReplay(MatchInfo matchInfo) {
        ODotaDataRequester oDotaDataRequester = new ODotaDataRequester();
        oDotaDataRequester.setMethod("matches");
        oDotaDataRequester.setPathParams(matchInfo.getMatchId());

        boolean returnVal = false;
        try {
            returnVal = oDotaDataRequester.downloadMatchInfoAndReplay("matchInfos" + File.separator + matchInfo.getMatchId() + ".txt",
                    "replays" + File.separator + matchInfo.getMatchId() + ".dem.bz2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnVal;
    }

    private void crawl() {
        prepareInitialData();
        prepareDataSet();
        System.out.println("******* Data Set Prepared ");

        writeMatchIds(matchIdFile, trainingMatchIds);
    }

    public static void main(String[] args1) {
//        if (args.length < 5) {
//            System.out.println("Error... Not enough arguments specified");
//            return;
//        }

        String dir = System.getProperty("user.dir");

        String[] args = { dir + File.separator + "src" + File.separator + "seedFile2.txt" , "3000", "500", dir + File.separator + "outFile.txt", ""};

        Crawler crawler = new Crawler();
        crawler.seedFilePath = args[0];
        crawler.thresholdMMR = Integer.parseInt(args[1]);
        crawler.dataSize = Integer.parseInt(args[2]);
        crawler.matchIdFile = args[3];
        crawler.replayDir = args[4];

        crawler.crawl();
    }
}
