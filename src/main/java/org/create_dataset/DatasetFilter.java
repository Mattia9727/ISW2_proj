package org.create_dataset;

import org.create_dataset.models.Version;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DatasetFilter {

    private DatasetFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static void filterVersionsByDate(List<Version> versions)  {
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

        int newSize = 0;

        // Tronco lista di versioni prendendo come ultima versione la prima da escludere
        for (newSize=0; newSize< versions.size(); newSize++) {
            if (halfDate.isBefore(versions.get(newSize).getDate())) {
                break;
            }
        }
        final int currentSize = versions.size();
        for (int i = currentSize - 1; i >= newSize+1; i--) {
            versions.remove(i);
        }
    }

    public static boolean checkIfJavaAndNotTest(String filename){
        String[] modFilename = filename.split("/");
        String realFilename = modFilename[modFilename.length-1];
        // TODO: Split filename per fare check startswith(Test)
        return !realFilename.endsWith(".java") || realFilename.endsWith("Test.java") || realFilename.endsWith("Tests.java") || realFilename.endsWith("TestCase.java") || realFilename.startsWith("Test");
    }

    public static void removeVersionsWithoutCommits(List<Version> versions){
        for (int i=0; i< versions.size()-1; i++){
            if (versions.get(i).getCommitList().size() == 0){
                versions.remove(i);
                i--;
            }
        }
    }

}
