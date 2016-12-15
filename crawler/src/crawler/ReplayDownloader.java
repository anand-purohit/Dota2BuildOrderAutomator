package crawler;

import java.io.File;
import java.util.HashSet;


/**
 * Created by Guest on 12/4/16.
 */
public class ReplayDownloader {


    private HashSet<String> MatchIds;
    private String outFilePath;
    private String replayDir;


    public void download(Crawler crawler) {

        MatchIds = crawler.readSeedFile(outFilePath);
        System.out.println(MatchIds.size());
        int count = 0;
        for(String id: MatchIds){
            MatchInfo matchInfo = crawler.getMatchInfo(id);
            if (!isReplayAlreadyExists(id) && crawler.downloadReplay(matchInfo)) {
                count++;
                System.out.println("Replays downloaded - " + count);
            }
            System.out.println("---------");
        }


    }

    public boolean isReplayAlreadyExists(String matchId) {
        File replay = new File(replayDir + matchId + ".dem");
        if (replay.exists()) {
            return true;
        }

        replay = new File(replayDir + matchId + ".dem.bz2");
        return replay.exists();
    }


    public static void main(String[] args) {


        String dir = System.getProperty("user.dir");
        ReplayDownloader replayDownloader = new ReplayDownloader();
        replayDownloader.outFilePath = dir + File.separator + "outFile.txt";
        replayDownloader.replayDir = dir + File.separator + "replays" + File.separator;
        System.out.println(replayDownloader.outFilePath);
        Crawler crawler = new Crawler();
        replayDownloader.download(crawler);

    }

}
