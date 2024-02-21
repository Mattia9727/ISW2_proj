package org.ml_se;

import org.ml_se.models.Bug;
import org.ml_se.models.Commit;
import org.ml_se.models.VersionRelease;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.ml_se.JiraManager.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String currentPath = System.getProperty("user.dir");
        String configFilePath = currentPath + File.separator + "src/main/java/org/ml_se/config/config.properties";
        Logger logger = Logger.getLogger(Main.class.getName());
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            logger.info("Nessun file properties trovato");
            return;
        }

        // Ottieni le stringhe dalla classe Properties
        String projName = properties.getProperty("projName");
        String pathname = properties.getProperty("pathname");

        DatasetRetriever dr = new DatasetRetriever(pathname);
        FeatureRetriever fr = new FeatureRetriever(pathname);

        List<VersionRelease> versionReleases = getReleasesFromJira(projName); //Recupera nomi versioni con data di rilascio
        List<Bug> bugs = getIssuesFromJira(projName);

        LocalDate lastDate = DatasetFilter.filterVersionsByDate(versionReleases);

        DatasetFilter.filterBugsByDate(bugs, lastDate);

        Proportion prop = new Proportion();
        for (Bug b : bugs){
            b.retrieveOvFvVersions(versionReleases);
        }
        prop.setIvWithProportion(bugs);
        for (Bug b : bugs){
            b.retrieveVersionsByIvFv(versionReleases);
        }

        List<Commit> gitCommits = dr.getCommits();
        dr.assignBugsToCommits(bugs,gitCommits);
        dr.assignCommitsToVersion(versionReleases,gitCommits);
        dr.retrieveDiffListPerVersion(versionReleases);
        dr.retrieveHashDifferences(versionReleases);
        dr.retrieveBuggyHashDiffs(versionReleases);

        fr.retrieveAllFeatures(versionReleases,pathname);

        CSVManager csv = new CSVManager(projName, pathname);
        csv.generateCSVsFromVersions(versionReleases);

        WekaManager wm = new WekaManager(projName);
        wm.predict(versionReleases);


    }
}
