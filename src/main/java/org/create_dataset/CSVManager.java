package org.create_dataset;

import org.apache.commons.io.FileUtils;
import org.create_dataset.models.HashDifference;
import org.create_dataset.models.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVManager {

    private void initCSV(String projName) throws IOException {
        File csvOutput = new File(projName+".csv");
        FileUtils.writeStringToFile(csvOutput, "version;filename;loc;loc_touched;NR;NFix;NAuth;LOC_added;MAX_LOC_added;AVG_LOC_added\n", false);
    }

    public void generateCSVFromVersions(String projName, String pathname,  List<Version> versions) throws IOException {
        File csvOutput = new File(projName+".csv");
        FileUtils.writeStringToFile(csvOutput, "version;oldfilename;newfilename;loc;loc_touched;NR;NFix;NAuth;LOC_added;MAX_LOC_added;AVG_LOC_added\n", false);

        for (int i=1; i<versions.size()-1; i++){
            for (HashDifference h : versions.get(i).getHashDiffs()){
                FileUtils.writeStringToFile(csvOutput, versions.get(i).getName() + ";" + h.getOldClassName() + ";" +
                    h.getNewClassName() + ";" +
                    h.getLines() + ";" + h.getLocTouched() + ";" +
                    h.getNumberOfRevisions() + ";_;" + h.getNAuthors() + ";" +
                    h.getAddedLines() + ";" + h.getMaxLocAdded() + ";" + h.getAvgLocAdded() + "\n", true);
            }
        }
    }
}
