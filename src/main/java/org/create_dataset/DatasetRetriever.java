package org.create_dataset;

import org.create_dataset.models.Bug;
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

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

import static org.create_dataset.DatasetFilter.filterDifflist;



public class DatasetRetriever {

    Logger logger = Logger.getLogger(getClass().getName());

    private static final String GIT = "\\.git";  // Compliant

    // Init stringhe usate
    String pathname;
    List<RevCommit> commitList = new ArrayList<>();

    public DatasetRetriever(String pathname) {

        this.pathname = pathname;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }



    // Prendo tutti i commit in Git e metto hash e date nella lista gitCommits
    public List<Commit> getCommits() throws IOException, GitAPIException {

        File path = new File(this.pathname + GIT);
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

                String comment = c.getShortMessage();

                Commit commit = new Commit(String.valueOf(i), c.getName(), cDate, comment);

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


    public void assignBugsToCommits(List<Bug>bugs, List<Commit>commits){
        for (Commit c : commits){
            for (Bug b : bugs){
                if (c.getComment().contains(b.getIssueName())) c.setBug(b);
            }
        }
    }

    private List<EditList>retrieveEditListFromDiffList(DiffFormatter df, List<DiffEntry> diffList) throws IOException {
        List<EditList> editLists = new ArrayList<>();
        for (DiffEntry d : diffList){
            EditList edList = df.toFileHeader(d).toEditList();
            editLists.add(edList);
        }
        return editLists;
    }

    public void retrieveDiffListPerVersion(List<Version> versions){
        OutputStream outS = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(outS);

        // Definisco repo e init git
        try (Git git = Git.open(new File(pathname + GIT))) {
            Repository repository = git.getRepository();
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            for (int i = 1; i < versions.size()-1; i++) {
                String beforeHash = versions.get(i - 1).getHash();
                String afterHash = versions.get(i).getHash();
                versions.get(i).setDiffList(filterDifflist(git.diff().setShowNameAndStatusOnly(true).setOldTree(Utils.prepareTreeParser(repository, beforeHash)).setNewTree(Utils.prepareTreeParser(repository, afterHash)).call()));
                List<EditList> editLists = retrieveEditListFromDiffList(df, versions.get(i).getDiffList());
                versions.get(i).setEditsList(editLists);
            }
        } catch (IOException | GitAPIException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void findBuggyHashDifferences(Version v, List<DiffEntry> diffList, Bug b){
        for (DiffEntry d : diffList) {
            String name = d.getNewPath();
            for (HashDifference h  : v.getHashDiffs()){
                if (h.getNewClassName().equals(name) && b.getVersions().contains(v.getName())) h.setBuggy(true);
            }
        }
    }

    public void retrieveBuggyHashDiffs(List<Version> versions){
        OutputStream outS = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(outS);

        // Definisco repo e init git
        try (Git git = Git.open(new File(pathname + GIT))) {
            Repository repository = git.getRepository();
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            for (Version v : versions.subList(1,versions.size()-1)) {
                for (int i = 1; i < v.getCommitList().size() - 1; i++) {
                    if (v.getCommitList().get(i).getBug() == null) continue;
                    String beforeHash = v.getCommitList().get(i - 1).getHash();
                    String afterHash = v.getCommitList().get(i).getHash();
                    List<DiffEntry> diffList = filterDifflist(git.diff().setShowNameAndStatusOnly(true).setOldTree(Utils.prepareTreeParser(repository, beforeHash)).setNewTree(Utils.prepareTreeParser(repository, afterHash)).call());
                    if (!diffList.isEmpty()) findBuggyHashDifferences(v, diffList, v.getCommitList().get(i).getBug());
                }
            }
        } catch (IOException | GitAPIException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static HashDifference findOldVersionHashDiff(List<Version> versions, String oldFilename, int start){
        for (int i = start-1; i>0; i--){
            for (HashDifference h : versions.get(i).getHashDiffs()) {
                if (h.getNewClassName().compareTo(oldFilename) == 0) { //Se tra gli hashdifference dell'ultima versione trovo questo file, ne creo uno nuovo a partire dal vecchio
                    return h;
                }
            }
        }
        return null;
    }

    public void addHashDifferenceToList(HashDifference oldVersionHashDiff, List<HashDifference> hashDiffs, String oldFilename, String newFilename, String afterHash) {
        HashDifference newH = new HashDifference(oldFilename, newFilename, afterHash);
        if (oldVersionHashDiff != null) {
            newH.setLines(oldVersionHashDiff.getLines());
        }
        hashDiffs.add(newH);
    }

    public void retrieveHashDifferences(List<Version> versions){
        HashDifference foundHD;
        for (int i = 1; i < versions.size()-1; i++) {
            List<HashDifference> hashDiffs = new ArrayList<>();
            String afterHash = versions.get(i).getHash();
            for (DiffEntry d : versions.get(i).getDiffList()) {
                String newFilename = d.getNewPath(); // Prende path del file considerato
                String oldFilename = d.getOldPath(); // Prende path del file considerato

                // Se non è un file java o è un file di test scarto
                if (DatasetFilter.checkIfJavaAndNotTest(newFilename)) continue;

                foundHD = null;
                if (!Objects.equals(oldFilename, "/dev/null")){
                    if (!Objects.equals(oldFilename, newFilename)){
                        logger.info("Rename!");
                    }
                    foundHD = findOldVersionHashDiff(versions, oldFilename, i);
                }

                addHashDifferenceToList(foundHD, hashDiffs, oldFilename, newFilename, afterHash);
            }
            versions.get(i).setHashDiffs(hashDiffs);
        }
    }
}
