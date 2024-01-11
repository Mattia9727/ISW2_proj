package org.create_dataset;

import org.apache.commons.io.FileUtils;
import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CSVManager {

    public void generateCSVFromVersions(String projName, List<Version> versions) throws IOException {
        File csvOutput = new File(projName+".csv");
        FileUtils.writeStringToFile(csvOutput, "version;oldfilename;newfilename;loc;loc_touched;loc_added;loc_removed;max_loc_added;avg_loc_added;churn;max_churn;avg_churn;NR;nAuthors;buggy\n", false);

        for (int i=1; i<versions.size()-1; i++){
            for (HashDifference h : versions.get(i).getHashDiffs()){
                FileUtils.writeStringToFile(csvOutput, versions.get(i).getName() + ";" + h.getOldClassName() + ";" +
                    h.getNewClassName() + ";" +
                    h.getLines() + ";" + h.getLocTouched() + ";" +
                    h.getAddedLines() + ";" + h.getRemovedLines() + ";" +
                    h.getMaxLocAdded() + ";" + h.getAvgLocAdded() + ";" + h.getChurn() + ";" + h.getMaxChurn()
                        + ";" + h.getAvgChurn() + ";" + h.getNumberOfRevisions() + ";" + h.getnAuthors() + ";" + h.isBuggy() + "\n", true);
            }
        }
    }
}
