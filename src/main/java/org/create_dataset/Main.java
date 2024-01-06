package org.create_dataset;

import org.create_dataset.models.Commit;
import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JSONException, GitAPIException {

        // Init stringhe usate
//        String projName = "OPENJPA";            //Nome progetto
//        String projID = "12310351";             //ID progetto
//        String pathname = ".\\resources\\openjpa32x";
        String projName = "BOOKKEEPER";            //Nome progetto
        String projID = "12310351";             //ID progetto
        String pathname = ".\\resources\\bookkeeper416";
//        Utils.getIssues(projName);
        DatasetRetriever dr = new DatasetRetriever(projName,projID,pathname);
        FeatureRetriever fr = new FeatureRetriever(pathname);

        List<Version> versions = dr.getReleasesFromJira(); //Recupera nomi versioni con data di rilascio
        DatasetFilter.filterVersionsByDate(versions);

        List<Commit> gitCommits = dr.getCommits();
        dr.assignCommitsToVersion(versions,gitCommits);
        dr.retrieveDiffListPerVersion(versions);
        dr.retrieveHashDifferences(versions);

        CSVManager csv = new CSVManager();
        csv.generateCSVFromVersions(projName, pathname, versions);

        System.out.println("AO");

//        for (HashDifference h: ){
//            System.out.println("AO");
//        }


    }
}
