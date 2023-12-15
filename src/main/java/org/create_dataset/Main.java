package org.create_dataset;

import org.apache.commons.io.FileUtils;
import org.create_dataset.models.Commit;
import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JSONException, GitAPIException {

        // Utils utils = new Utils();

        // Init stringhe usate
        String projName = "openjpa";            //Nome progetto
        String projID = "12310351";             //ID progetto
        String pathname = ".\\resources\\openjpa32x";

        DatasetRetriever dr = new DatasetRetriever(projName,projID,pathname);

        List<Version> versions = dr.getReleasesFromJira(); //Recupera nomi versioni con data di rilascio
        List<Version> filteredVersions = DatasetFilter.filterVersionsByDate(versions);
        List<Commit> gitCommits = dr.getCommits();


    }
}
