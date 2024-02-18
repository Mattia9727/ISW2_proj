package org.predict_bugginess;

public class Main {

    public static void main(String[] args){
        try {
            //Creo il file csv
            CsvCreator dataFile = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Release","Class","LOC","NR","NAuth",
                    "Age","ChgSetSize","MAX_ChgSetSize","AVG_ChgSetSize",
                    "Churn","MAX_Churn","AVG_Churn","Bugginess"});
            dataFile.writeDataOnCsv(releases);

            CsvCreator metricsFile = new CsvCreator("weka_"+PROJECTNAME.toLowerCase()+".csv",
                    new String[]{"Dataset","Classifier","#TrainingRelease","%Training", "sensitivity","featureSelection",
                            "sampling","TP","FP","TN","FN","Precision","Recall","Kappa","AUC"});

            List<String[]> results;
            String[] technics;
            String backwardSearch = "Backward search";
            String oversampling = "oversampling";
            String undersampling = "undersampling";
            String sensitivityLearning = "sensitivity learning";
            String sensitivityThreshold = "sensitivity threshold";
            Classificator naiveBayes;
            Classificator ibk;
            Classificator randomForest;

            //Validation without feature selection and without sampling
            technics = new String[]{"no","no selection","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME, releases, null, false,null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME, releases, null, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null,false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and without sampling
            technics = new String[]{"no",backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null,true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation without feature selection and with undersampling
            technics = new String[]{"no","no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation without feature selection and with oversampling
            technics = new String[]{"no","no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,"no","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,"no","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and with undersampling
            technics = new String[]{"no",backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and with oversampling
            technics = new String[]{"no",backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with oversampling and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,"no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with oversampling and cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,"no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            //Validation with undersampling and cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,"no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with undersampling and cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,"no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            //Validation with feature selection with oversampling and cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with oversampling and cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with undersampling and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivityLearning,backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with undersampling and cost sensitivity threshold
            technics = new String[]{sensitivityThreshold,backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            metricsFile.closeFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
