package org.ml_se;

import org.ml_se.models.HashDifference;
import org.ml_se.models.VersionRelease;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ArffConverter {
    private File file;
    private String title;
    private String[] header = new String[]{"LOC numeric","LOC_Touched numeric","LOC_Added numeric",
            "MAX_LOC_Added numeric","AVG_LOC_Added numeric","Churn numeric",
           "MAX_Churn numeric","AVG_Churn numeric","NR numeric","N_Authors numeric","Bugginess {'true', 'false'}"};

    public ArffConverter(String filePath, String title) {
        this.file = new File(filePath);
        this.title = title;
    }


    public void writeData(List<VersionRelease> versionReleases, boolean isTrainingSet) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        //Titolo
        printWriter.println("@relation "+this.title);
        //Header
        for(String attribute: this.header){
            printWriter.println("@attribute "+attribute);
        }
        // Dati
        printWriter.println("@data");


        int vCount = 1;
        for(VersionRelease v: versionReleases){
            for(HashDifference hd: v.getHashDiffs()){
                boolean isBuggy;
                if(isTrainingSet){
                    if (hd.getFv()<=vCount) isBuggy=hd.isBuggy();
                    else isBuggy=false;
                }
                else isBuggy = hd.isBuggy();
                String data = hd.getLines() +","+
                        hd.getLocTouched() +","+
                        hd.getAddedLines() +","+
                        hd.getMaxLocAdded() +","+
                        hd.getAvgLocAdded() +","+
                        hd.getChurn() +","+
                        hd.getMaxChurn() +","+
                        hd.getAvgChurn() +","+
                        hd.getNumberOfRevisions() +","+
                        hd.getnAuthors() +","+
                        isBuggy +",";
                printWriter.println(data);
            }
            vCount++;
        }

        printWriter.close();
    }

}
