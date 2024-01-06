package org.create_dataset;

import org.create_dataset.models.Commit;
import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Locale.ITALIAN;
import static org.create_dataset.Utils.readJsonFromUrl;

public class DatasetRetriever {

    // Init stringhe usate
    String projName;
    String projID;
    String pathname;
    List<RevCommit> commitList = new ArrayList<>();

    public DatasetRetriever(String projName, String projID, String pathname) {
        this.projName = projName;
        this.projID = projID;
        this.pathname = pathname;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjID() {
        return projID;
    }

    public void setProjID(String projID) {
        this.projID = projID;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public List<Version> getReleasesFromJira() throws IOException, JSONException {
        List<Version> versions = new ArrayList<>();
        Version v = new Version(LocalDate.ofEpochDay(0),"init");
        versions.add(v);

        String versionsUrl = "https://issues.apache.org/jira/rest/api/2/project/" + this.projName;
        JSONObject jsons = readJsonFromUrl(versionsUrl);
        JSONArray values = jsons.getJSONArray("versions");

        for (int i = 0; i < values.length(); i++) {
            if (!values.getJSONObject(i).has("releaseDate")) {
                continue;
            }
            String name = values.getJSONObject(i).getString("name");
            String releaseDate = values.getJSONObject(i).getString("releaseDate");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale(ITALIAN);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            LocalDate date = LocalDate.parse(releaseDate, formatter);

            v = new Version(date, name);
            versions.add(v);
        }

        versions.sort(Comparator.comparing(Version::getDate));

        return versions;
    }

    // Prendo tutti i commit in Git e metto hash e date nella lista gitCommits
    public List<Commit> getCommits() throws IOException, GitAPIException {

        File path = new File(this.pathname + "\\.git");
        try (Git git = Git.open(path)) {
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve("HEAD");

            List<Commit> gitCommits = new ArrayList<>();
            Iterable<RevCommit> commits = git.log().add(head).call();

            int i = 1;

            for (RevCommit c : commits) {

                PersonIdent authorIdent = c.getAuthorIdent();
                Date commitDate = authorIdent.getWhen();
                LocalDate cDate = commitDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                Commit commit = new Commit(String.valueOf(i), c.getName(), cDate);

                gitCommits.add(commit);
                i += 1;
                this.commitList.add(c);
            }
            gitCommits.sort(Comparator.comparing(Commit::getDate));
            return gitCommits;
        }
    }


    public void assignCommitsToVersion(List<Version> versions, List<Commit> commits) {

        LocalDate firstVersionDate = versions.get(0).getDate();
        LocalDate lastVersionDate = versions.get(versions.size() - 1).getDate();
        for (Commit c : commits) {
            if (c.getDate().isBefore(firstVersionDate) || lastVersionDate.isBefore(c.getDate())) {
                continue;
            }
            for (int i = 0; i < versions.size() - 1; i++) {
                LocalDate cDate = c.getDate();
                LocalDate vDateBefore = versions.get(i).getDate().minusDays(1);
                LocalDate vDateAfter = versions.get(i + 1).getDate();
                if (vDateBefore.isBefore(cDate) && vDateAfter.isAfter(cDate)) {
                    versions.get(i).getCommitList().add(c);
                    break;
                }
            }
        }
        DatasetFilter.removeVersionsWithoutCommits(versions);
    }

    public void retrieveDiffListPerVersion(List<Version> versions) throws IOException {


        try (Git git = Git.open(new File(this.pathname + "\\.git"))) {
            Repository repository = git.getRepository();
            for (int i = 1; i < versions.size()-1; i++) {
                String beforeHash = versions.get(i - 1).getHash();
                String afterHash = versions.get(i).getHash();
                versions.get(i).setDiffList(git.diff().setShowNameAndStatusOnly(true).setOldTree(Utils.prepareTreeParser(repository, beforeHash)).setNewTree(Utils.prepareTreeParser(repository, afterHash)).call());
            }
        } catch (IOException | GitAPIException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public HashDifference findOldVersionHashDiff(List<Version> versions, String oldFilename, int start){
        for (int i = start-1; i>0; i--){
            for (HashDifference h : versions.get(i).getHashDiffs()) {
                if (h.getNewClassName().compareTo(oldFilename) == 0) { //Se tra gli hashdifference dell'ultima versione trovo questo file, ne creo uno nuovo a partire dal vecchio
                    return h;
                }
            }
        }
        System.out.println("QUALCOSA NON VA");
        return null;
    }

    public void addHashDifferenceToList(HashDifference oldVersionHashDiff, List<HashDifference> hashDiffs, String oldFilename, String newFilename, String beforeHash, String afterHash) {
        HashDifference newH = new HashDifference(oldFilename, newFilename, beforeHash, afterHash, 0, 0, 0, 0);
        if (oldVersionHashDiff != null) {
            newH.setLines(oldVersionHashDiff.getLines());
        }
        hashDiffs.add(newH);
    }

    public void retrieveHashDifferences(List<Version> versions) throws IOException {
        HashDifference foundHD = null;
        for (int i = 1; i < versions.size()-1; i++) {
            List<HashDifference> hashDiffs = new ArrayList<>();
            String beforeHash = versions.get(i - 1).getHash();
            String afterHash = versions.get(i).getHash();
            for (DiffEntry d : versions.get(i).getDiffList()) {
                String newFilename = d.getNewPath(); // Prende path del file considerato
                String oldFilename = d.getOldPath(); // Prende path del file considerato

                // Se non è un file java o è un file di test scarto
                if (DatasetFilter.checkIfJavaAndNotTest(newFilename)) continue;

                foundHD = null;
                if (!Objects.equals(oldFilename, "/dev/null")){
                    if (!Objects.equals(oldFilename, newFilename)){
                        System.out.println(("Rename!"));
                    }
                    foundHD = findOldVersionHashDiff(versions, oldFilename, i);
                }

                addHashDifferenceToList(foundHD, hashDiffs, oldFilename, newFilename, beforeHash, afterHash);
                FeatureRetriever fr = new FeatureRetriever(pathname);
                fr.retrieveLines(hashDiffs.get(hashDiffs.size() - 1), d);
            }
            versions.get(i).setHashDiffs(hashDiffs);
            System.out.println("Fatta versione "+ i);
        }
    }
}


//    private void bo(List<HashDifference> hashDiffs) {
//        EditList edList = df.toFileHeader(d).toEditList();  // Trovo la lista delle modifiche del file tra un commit e il successivo
//        int addLines = 0;
//        int removedLines = 0;
//        int maxLOCAdded = 0;
//        int locVariation;
//
//        for (Edit edit : edList) {
//            if (edit.getType() == Edit.Type.INSERT) {
//                locVariation = edit.getEndB() - edit.getBeginB();
//                addLines += locVariation;
//                if (maxLOCAdded <= locVariation) {
//                    maxLOCAdded = locVariation;
//                }
//            } else if (edit.getType() == Edit.Type.DELETE) {
//                locVariation = edit.getEndA() - edit.getBeginA();
//                removedLines += locVariation;
//            } else if (edit.getType() == Edit.Type.REPLACE) {
//                locVariation = edit.getEndB() - edit.getBeginB();
//                addLines += locVariation;
//                locVariation = edit.getEndA() - edit.getBeginA();
//                removedLines += locVariation;
//            }
//
//            //                        if (edit.getType() == Edit.Type.EMPTY) {
//            //                            hashDiffs.get(j).setAddedLines(0);
//            //                            hashDiffs.get(j).setRemovedLines(0);
//            //                        }
//
//        }
//
//
//        hashDiffs.get(j).setAddedLines(addLines);
//        hashDiffs.get(j).setRemovedLines(removedLines);
//        hashDiffs.get(j).setLines(hashDiffs.get(j).getLines() + addLines - removedLines);
//        hashDiffs.get(j).setNumberOfRevisions(hashDiffs.get(j).getNumberOfRevisions() + edList.size());
//        int locTouched = hashDiffs.get(j).getAddedLines() + hashDiffs.get(j).getRemovedLines();
//        int avgLOCAdded = hashDiffs.get(j).getAddedLines() / edList.size();
//        FileUtils.writeStringToFile(csvOutput, versions.get(i).getName() + ";" + hashDiffs.get(j).getClassName() + ";" +
//                hashDiffs.get(j).getLines() + ";" + locTouched + ";" +
//                hashDiffs.get(j).getNumberOfRevisions() + ";_;" + nAuthors + ";" +
//                hashDiffs.get(j).getAddedLines() + ";" + maxLOCAdded + ";" + avgLOCAdded + "\n", true);
//    }