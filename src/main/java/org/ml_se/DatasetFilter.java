package org.ml_se;

import org.ml_se.models.Bug;
import org.ml_se.models.VersionRelease;
import org.eclipse.jgit.diff.DiffEntry;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DatasetFilter {

    private DatasetFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate filterVersionsByDate(List<VersionRelease> versionReleases)  {
        // Prendo la prima e l'ultima data delle release trovate
        LocalDate firstDate = versionReleases.get(1).getDate();
        LocalDate lastDate = versionReleases.get(1).getDate();
        for (int i = 2; i< versionReleases.size(); i++){
            LocalDate compareDate = versionReleases.get(i).getDate();
            if (firstDate.isAfter(compareDate)) {firstDate = compareDate;}
            if (lastDate.isBefore(compareDate)) {lastDate = compareDate;}
        }

        // Trovo la data di mezzo
        long daysBetween = DAYS.between(firstDate, lastDate)/2;
        LocalDate halfDate = firstDate.plusDays(daysBetween);

        int newSize;

        // Tronco lista di versioni prendendo come ultima versione la prima da escludere
        for (newSize=0; newSize< versionReleases.size(); newSize++) {
            if (halfDate.isBefore(versionReleases.get(newSize).getDate())) {
                break;
            }
        }
        if (versionReleases.size() > newSize + 1) {
            versionReleases.subList(newSize + 1, versionReleases.size()).clear();
        }
        return halfDate;
    }

    public static void filterBugsByDate(List<Bug> bugs, LocalDate date){

        bugs.removeIf(b -> b.getOv().isAfter(date));

    }

    public static boolean checkIfJavaAndNotTest(String filename){
        return !filename.endsWith(".java") || filename.contains("test") || filename.contains("Test");
    }

    public static void removeVersionsWithoutCommits(List<VersionRelease> versionReleases){
        versionReleases.removeIf(version -> version.getCommitList().isEmpty());
    }

    public static List<DiffEntry> filterDifflist(List<DiffEntry> diffList){
        diffList.removeIf(diffEntry -> checkIfJavaAndNotTest(diffEntry.getNewPath()));


        return diffList;
    }

}
