package org.create_dataset;

import org.create_dataset.models.Version;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DatasetFilter {

    private DatasetFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Version> filterVersionsByDate(List<Version> versions)  {
        // Prendo la prima e l'ultima data delle release trovate
        LocalDate firstDate = versions.get(0).getDate();
        LocalDate lastDate = versions.get(0).getDate();
        for (int i=1; i< versions.size(); i++){
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
        return versions;
    }
}
