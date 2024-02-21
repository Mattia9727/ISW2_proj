package org.ml_se;

import org.ml_se.models.VersionRelease;
import org.ml_se.enums.Classificators;
import org.ml_se.enums.CostSensitivity;
import org.ml_se.enums.Sampling;

import java.util.ArrayList;
import java.util.List;



public class WekaManager {

    private static final String NO_CS = "No cs";  // Compliant
    private static final String NO_SELECTION = "No selection";  // Compliant
    private static final String NO_SAMPLING = "No sampling";  // Compliant
    private static final String BACKWARD_SEARCH = "Backward search";
    private static final String OVERSAMPLING = "Oversampling";
    private static final String UNDERSAMPLING = "Undersampling";
    private static final String SENSITIVITY_LEARNING = "Sensitivity learning";
    private static final String SENSITIVITY_THRESHOLD = "Sensitivity threshold";

    public final String projName;

    public WekaManager(String projName) {
        this.projName = projName;
    }

    public void predict(List<VersionRelease> versionReleases) throws Exception {

        String csvPathName = "weka_metrics_"+projName+".csv";
        CSVManager csv = new CSVManager(projName, csvPathName);
        csv.createWekaMetricsCSV();

        List<String[]> results;

        WekaEvaluation naiveBayes;
        WekaEvaluation ibk;
        WekaEvaluation randomForest;

        versionReleases = versionReleases.subList(1, versionReleases.size()-1);
        if (projName.equals("OPENJPA")){
            versionReleases = versionReleases.subList(0, versionReleases.size()-1);
        }

        List<String[]> technicsStrings = new ArrayList<>();
        technicsStrings.add(new String[]{NO_CS,NO_SELECTION,NO_SAMPLING});
        technicsStrings.add(new String[]{NO_CS, BACKWARD_SEARCH,NO_SAMPLING});
        technicsStrings.add(new String[]{NO_CS,NO_SELECTION, UNDERSAMPLING});
        technicsStrings.add(new String[]{NO_CS,NO_SELECTION, OVERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING,NO_SELECTION,NO_SAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD,NO_SELECTION,NO_SAMPLING});
        technicsStrings.add(new String[]{NO_CS, BACKWARD_SEARCH, UNDERSAMPLING});
        technicsStrings.add(new String[]{NO_CS, BACKWARD_SEARCH, OVERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING, BACKWARD_SEARCH,NO_SAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD, BACKWARD_SEARCH,NO_SAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING,NO_SELECTION, UNDERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD,NO_SELECTION, UNDERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING,NO_SELECTION, OVERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD,NO_SELECTION, OVERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING, BACKWARD_SEARCH, UNDERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD, BACKWARD_SEARCH, UNDERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_LEARNING, BACKWARD_SEARCH, OVERSAMPLING});
        technicsStrings.add(new String[]{SENSITIVITY_THRESHOLD, BACKWARD_SEARCH, OVERSAMPLING});

        for (String[] technics : technicsStrings){

            CostSensitivity cs;
            boolean fs;
            Sampling s;

            if (technics[0].equals(SENSITIVITY_THRESHOLD)) cs = CostSensitivity.SENSITIVITY_THRESHOLD;
            else if (technics[0].equals(SENSITIVITY_LEARNING)) cs = CostSensitivity.SENSITIVITY_LEARNING;
            else cs = null;

            if (technics[1].equals(NO_SELECTION)) fs = false;
            else fs = true;

            if (technics[2].equals(UNDERSAMPLING)) s = Sampling.UNDERSAMPLING;
            else if (technics[2].equals(OVERSAMPLING)) s = Sampling.OVERSAMPLING;
            else s = null;

            naiveBayes = new WekaEvaluation(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForward(projName, versionReleases, cs, fs,s);
            csv.writeDataOnWekaCsv(projName, Classificators.NAIVEBAYES,technics,results);

            ibk = new WekaEvaluation(Classificators.IBK);
            results = ibk.walkForward(projName, versionReleases, cs, fs, s);
            csv.writeDataOnWekaCsv(projName, Classificators.IBK,technics,results);

            randomForest = new WekaEvaluation(Classificators.RANDOMFOREST);
            results = randomForest.walkForward(projName,versionReleases, cs,fs, s);
            csv.writeDataOnWekaCsv(projName, Classificators.RANDOMFOREST,technics,results);

        }
    }
}
