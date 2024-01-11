package org.create_dataset;

import org.create_dataset.models.Bug;
import org.create_dataset.models.Commit;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.create_dataset.JiraManager.*;

public class Main {

    public static void main(String[] args) throws IOException, JSONException, GitAPIException {
        String currentPath = System.getProperty("user.dir");
        String configFilePath = currentPath + File.separator + "src/main/java/org/create_dataset/config/config.properties";
        Logger logger = Logger.getLogger(Main.class.getName());
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            logger.info("Nessun file properties trovato");
        }

        // Ottieni le stringhe dalla classe Properties
        String projName = properties.getProperty("projName");
        String pathname = properties.getProperty("pathname");

        DatasetRetriever dr = new DatasetRetriever(pathname);
        FeatureRetriever fr = new FeatureRetriever(pathname);

        List<Version> versions = getReleasesFromJira(projName); //Recupera nomi versioni con data di rilascio
        List<Bug> bugs = getIssuesFromJira(projName);

        LocalDate lastDate = DatasetFilter.filterVersionsByDate(versions);

        DatasetFilter.filterBugsByDate(bugs, lastDate);

        Proportion prop = new Proportion();
        for (Bug b : bugs){
            b.retrieveOvFvVersions(versions);
        }
        prop.setIvWithProportion(bugs);

        for (Bug b : bugs){
            b.retrieveVersionsByIvFv(versions);
        }

        List<Commit> gitCommits = dr.getCommits();
        dr.assignBugsToCommits(bugs,gitCommits);
        dr.assignCommitsToVersion(versions,gitCommits);
        dr.retrieveDiffListPerVersion(versions);
        dr.retrieveHashDifferences(versions);
        dr.retrieveBuggyHashDiffs(versions);

        fr.retrieveAllFeatures(versions,pathname);


        CSVManager csv = new CSVManager();
        csv.generateCSVFromVersions(projName, versions);

    }
}
