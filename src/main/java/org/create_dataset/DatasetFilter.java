package org.create_dataset;

import org.create_dataset.models.Bug;
import org.create_dataset.models.Version;
import org.eclipse.jgit.diff.DiffEntry;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DatasetFilter {

    private DatasetFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate filterVersionsByDate(List<Version> versions)  {
        // Prendo la prima e l'ultima data delle release trovate
        LocalDate firstDate = versions.get(1).getDate();
        LocalDate lastDate = versions.get(1).getDate();
        for (int i=2; i< versions.size(); i++){
            LocalDate compareDate = versions.get(i).getDate();
            if (firstDate.isAfter(compareDate)) {firstDate = compareDate;}
            if (lastDate.isBefore(compareDate)) {lastDate = compareDate;}
        }

        // Trovo la data di mezzo
        long daysBetween = DAYS.between(firstDate, lastDate)/2;
        LocalDate halfDate = firstDate.plusDays(daysBetween);

        int newSize;

        // Tronco lista di versioni prendendo come ultima versione la prima da escludere
        for (newSize=0; newSize< versions.size(); newSize++) {
            if (halfDate.isBefore(versions.get(newSize).getDate())) {
                break;
            }
        }
        if (versions.size() > newSize + 1) {
            versions.subList(newSize + 1, versions.size()).clear();
        }
        return halfDate;
    }

    public static void filterBugsByDate(List<Bug> bugs, LocalDate date){

        bugs.removeIf(b -> b.getOv().isAfter(date));

    }

    public static boolean checkIfJavaAndNotTest(String filename){
        return !filename.endsWith(".java") || filename.contains("test") || filename.contains("Test");
    }

    public static void removeVersionsWithoutCommits(List<Version> versions){
        versions.removeIf(version -> version.getCommitList().isEmpty());
    }

    public static List<DiffEntry> filterDifflist(List<DiffEntry> diffList){
        diffList.removeIf(diffEntry -> checkIfJavaAndNotTest(diffEntry.getNewPath()));


        return diffList;
    }

}
