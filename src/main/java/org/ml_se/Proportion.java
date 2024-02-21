package org.ml_se;

import org.ml_se.models.Bug;
import org.ml_se.models.VersionRelease;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ml_se.JiraManager.*;

public class Proportion {
    private final String[] otherProjNames = {"AVRO", "STORM", "ZOOKEEPER", "SYNCOPE", "TAJO"};


    private float calculateP(Bug b){
        int den = b.getFvVersion() - b.getOvVersion();
        if (den == 0) den = 1;
        return (float) (b.getFvVersion() - b.getIvVersion()) / den;
    }

    private int calculateIv(Bug b, float p){
        int den = b.getFvVersion() - b.getOvVersion();
        if (den == 0) return b.getFvVersion();
        else return ((int) Math.ceil(den * p));
    }

    private float coldStart() throws IOException {
        float p;
        List<Float> projAvgPs = new ArrayList<>();
        for (String proj : otherProjNames) {
            List<Float> intraProjPs = new ArrayList<>();
            List<VersionRelease> otherProjVersionReleases = getReleasesFromJira(proj); //Recupera nomi versioni con data di rilascio
            List<Bug> otherProjBugs = getIssuesFromJira(proj);
            for (Bug b : otherProjBugs) {
                b.retrieveOvFvVersions(otherProjVersionReleases);
                if (b.getIvVersion()!=0) intraProjPs.add(calculateP(b));
            }
            projAvgPs.add(Utils.calculateAvg(intraProjPs));
        }

        p = Utils.calculateMedian(projAvgPs);

        return p;
    }

    private void movingWindow(List<Bug> bugs, float coldstartP){
        int percent = bugs.size()/100;
        int calcIv;
        for (int i=0; i<bugs.size(); i++){
            if(!bugs.get(i).getVersions().isEmpty()) continue;   //Ho già iv, non devo trovarlo
            if (i<percent){                                     //Caso iniziale, devo usare il p del Cold Start
                calcIv = calculateIv(bugs.get(i), coldstartP);
                bugs.get(i).setIvVersion(calcIv);
            }
            else{                                               //Dopo posso usare p trovate via moving window
                List<Float> percentPs = new ArrayList<>();
                for (int j = i-percent; j<i; j++){
                    percentPs.add(calculateP(bugs.get(j)));
                }
                float movingWindowP = Utils.calculateAvg(percentPs);
                calcIv = calculateIv(bugs.get(i), movingWindowP);
                bugs.get(i).setIvVersion(calcIv);
            }
        }
    }


    public void setIvWithProportion(List<Bug> bugs) throws IOException {
        float firstP = coldStart();
        movingWindow(bugs,firstP);
        for(Bug b : bugs){
            if (b.getIvVersion()<=0) b.setIvVersion(1);
            if (b.getIvVersion()>b.getOvVersion()) b.setIvVersion(b.getOvVersion());
        }
    }
}

