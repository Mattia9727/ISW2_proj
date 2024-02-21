package org.ml_se;

import java.util.ArrayList;
import java.util.List;

import org.ml_se.models.VersionRelease;
import org.ml_se.enums.Classificators;
import org.ml_se.enums.CostSensitivity;
import org.ml_se.enums.Sampling;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.supervised.instance.SMOTE;


public class WekaEvaluation {

    private Classifier wekaClassifier;
    private static final String ARFF = ".arff";

    public WekaEvaluation(Enum<Classificators> classificator){
        switch(classificator.toString()){
            case "RANDOMFOREST":
                RandomForest rf = new RandomForest();
                this.wekaClassifier = rf;
                break;
            case "NAIVEBAYES":
                this.wekaClassifier = new NaiveBayes();
                break;
            case "IBK":
                this.wekaClassifier = new IBk();
                break;
            default:
                this.wekaClassifier = new ZeroR();
                break;
        }

    }

    public List<String[]> walkForward(String project, List<VersionRelease> versions, CostSensitivity costSensitivity, boolean featureSelection, Sampling sampling) throws Exception {
        String trainingSetPathname = "training-set-"+project;
        String testingSetPathname = "testing-set-"+project;
        List<String[]> results = new ArrayList<>();
        for(int i = 1; i < versions.size(); i++){
            // Definisco training e testing set
            ArffConverter trainingSet = new ArffConverter(trainingSetPathname+ ARFF, trainingSetPathname);
            ArffConverter testingSet = new ArffConverter(testingSetPathname+ ARFF, testingSetPathname);

            trainingSet.writeData(versions.subList(0,i),true);
            testingSet.writeData(List.of(versions.get(i)),false);

            Evaluation eval = calculateEvaluation(trainingSetPathname, testingSetPathname, costSensitivity, featureSelection, sampling);

            String[] evalResult = new String[8];
            evalResult[0] = String.valueOf(eval.numTruePositives(0));
            evalResult[1] = String.valueOf(eval.numFalsePositives(0));
            evalResult[2] = String.valueOf(eval.numTrueNegatives(0));
            evalResult[3] = String.valueOf(eval.numFalseNegatives(0));
            evalResult[4] = String.valueOf(eval.precision(0));
            evalResult[5] = String.valueOf(eval.recall(0));
            evalResult[6] = String.valueOf(eval.kappa());
            evalResult[7] = String.valueOf(eval.areaUnderROC(0));

            results.add(evalResult);
        }
        return results;
    }

    public Evaluation calculateEvaluation(String trainingFile, String testingFile, CostSensitivity costSensitivity, boolean featureSelection, Sampling sampling) throws Exception {
        Instances training = new DataSource(trainingFile+ ARFF).getDataSet();
        Instances testing = new DataSource(testingFile+ ARFF).getDataSet();

        if(featureSelection){
            //create AttributeSelection object
            AttributeSelection filter = new AttributeSelection();
            //create evaluator and search algorithm objects
            CfsSubsetEval eval = new CfsSubsetEval();
            GreedyStepwise search = new GreedyStepwise();
            //set the algorithm to search backward
            search.setSearchBackwards(true);
            filter.setEvaluator(eval);
            filter.setSearch(search);
            //specify the dataset
            filter.setInputFormat(training);
            //apply
            training = Filter.useFilter(training, filter);
            testing= Filter.useFilter(testing, filter);
        }

        training.setClassIndex(training.numAttributes() - 1);
        testing.setClassIndex(testing.numAttributes() - 1);
        this.wekaClassifier.buildClassifier(training);

        if(sampling != null) {
            FilteredClassifier fc = new FilteredClassifier();
            fc.setClassifier(this.wekaClassifier);
            Filter filter = null;
            switch (sampling) {
                case UNDERSAMPLING -> {
                    filter = new SpreadSubsample();
                    String[] opts = new String[]{"-M", "1.0"};
                    filter.setOptions(opts);
                }
                case OVERSAMPLING -> {
                    filter = new Resample();
                    filter.setOptions(new String[]{"-B", "1.0", "-Z", "130.3"});
                    filter.setInputFormat(training);
                }
                case SMOTE -> {
                    filter = new SMOTE();
                    filter.setInputFormat(training);

                }
            }
            fc.setFilter(filter);
            fc.buildClassifier(training);
            this.wekaClassifier = fc;
        }

        if(costSensitivity != null){
            CostSensitiveClassifier classifier = new CostSensitiveClassifier();
            CostMatrix matrix = new CostMatrix(2);
            matrix.setCell(0, 0, 0.0);
            matrix.setCell(0, 1, 1.0);
            matrix.setCell(1, 1, 0.0);
            if(costSensitivity == CostSensitivity.SENSITIVITY_LEARNING) {
                matrix.setCell(1, 0, 10.0);
                classifier.setCostMatrix(matrix);
                classifier.setMinimizeExpectedCost(false);
            }
            else{
                matrix.setCell(1, 0, 1.0);
                classifier.setCostMatrix(matrix);
                classifier.setMinimizeExpectedCost(true);
            }

            classifier.setClassifier(this.wekaClassifier);
            classifier.buildClassifier(training);
            this.wekaClassifier = classifier;
        }
        Evaluation eval = new Evaluation(testing);
        eval.evaluateModel(this.wekaClassifier, testing);

        return eval;
    }

}
