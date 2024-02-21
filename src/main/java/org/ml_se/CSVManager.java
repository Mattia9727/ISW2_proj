package org.ml_se;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.ml_se.models.HashDifference;
import org.ml_se.models.VersionRelease;
import org.ml_se.enums.Classificators;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CSVManager {

    String projName;
    String wekaPathname;

    public CSVManager(String projName, String wekaPathname) {
        this.projName = projName;
        this.wekaPathname = wekaPathname;
    }

    public void generateCSVFromVersions(List<VersionRelease> versions) throws IOException {
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

    public void generateCSVsFromVersions(List<VersionRelease> versions) throws IOException {

        for (int i=1; i<versions.size()-1; i++){
            File csvOutput = new File(projName+"_"+i+".csv");
            FileUtils.writeStringToFile(csvOutput, "version;filename;loc;loc_touched;loc_added;loc_removed;max_loc_added;avg_loc_added;churn;max_churn;avg_churn;NR;nAuthors;buggy\n", false);

            for (int j=1; j<=i; j++){
                for (HashDifference h : versions.get(j).getHashDiffs()){
                    boolean isBuggy=false;
                    if(h.isBuggy() && h.getOv()<=i) isBuggy=true;

                    FileUtils.writeStringToFile(csvOutput, versions.get(j).getName() + ";" +
                            h.getNewClassName() + ";" + h.getLines() + ";" + h.getLocTouched() + ";" +
                            h.getAddedLines() + ";" + h.getRemovedLines() + ";" + h.getMaxLocAdded() + ";" +
                            h.getAvgLocAdded() + ";" + h.getChurn() + ";" + h.getMaxChurn() + ";" +
                            h.getAvgChurn() + ";" + h.getNumberOfRevisions() + ";" + h.getnAuthors() + ";" + isBuggy + "\n", true);
                }
            }

        }
    }

    public void createWekaMetricsCSV() throws IOException {
        File csvOutput = new File(this.wekaPathname);
        FileUtils.writeStringToFile(csvOutput, "Dataset;Classifier;#TrainingRelease;%Training;sensitivity;featureSelection;sampling;TP;FP;TN;FN;Precision;Recall;Kappa;AUC\n", false);
    }

    public void writeDataOnWekaCsv(String project, Classificators classifier, String[] technics, List<String[]> results) throws IOException {
        File csvOutput = new File(wekaPathname);

        for(int i = 0; i < results.size(); i++){
            String[] configuration = ArrayUtils.addAll(new String[]{project,classifier.toString(),String.valueOf(i+1),
                    String.valueOf((int)(((double)i+1)/(results.size()+1)*100))},technics);
            for (String s : results.get(i)){
                configuration = ArrayUtils.addAll(configuration,s);
            }
            for (String s : configuration)
                FileUtils.writeStringToFile(csvOutput, s+";", true);
            FileUtils.writeStringToFile(csvOutput, "\n", true);

        }
    }

}
