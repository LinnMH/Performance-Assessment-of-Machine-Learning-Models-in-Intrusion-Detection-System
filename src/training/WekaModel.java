package training;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WekaModel {

    private String modelName;

    private Classifier classifier = null;

    public WekaModel(String modelName) {
        this.modelName = modelName;
        if (modelName.equalsIgnoreCase("j48")) {
            J48 j48 = new J48();
            j48.setConfidenceFactor(0.25f);
            j48.setMinNumObj(2);
            j48.setNumFolds(3);
            j48.setSeed(1);
            j48.setUnpruned(false);
            j48.setCollapseTree(true);
            j48.setSubtreeRaising(true);
            classifier = j48;
        } else if (modelName.equalsIgnoreCase("svm")) {
//            classifier = new SupportVectorMachineModel();
        } else if (modelName.equalsIgnoreCase("mlp")) {
            classifier = new MultilayerPerceptron();
        } else if (modelName.equalsIgnoreCase("naivebayes")) {
            classifier = new NaiveBayes();
        } else if (modelName.equalsIgnoreCase("bayesnet")) {
            classifier = new BayesNet();
        } else if (modelName.equalsIgnoreCase("randomforest")) {
            classifier = new RandomForest();
        }
    }

    public String getModelName() {
        return modelName;
    }

    public void train(String trainFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(trainFile));
        Instances train = new Instances(br);
        train.setClassIndex(train.numAttributes() -1);
        classifier.buildClassifier(train);
        br.close();
    }

    public void test(String testFile, String targetFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        Instances test = new Instances(br);
        test.setClassIndex(test.numAttributes() -1);
        for (int i = 0; i < test.size(); i++) {
            try {
                test.instance(i).setClassMissing();
                double label = classifier.classifyInstance(test.instance(i));
                test.instance(i).setClassValue(label);
            } catch (Exception e) {
                System.err.println(i + " : " + e.getMessage());
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
        bw.write(test.toString());
        bw.close();
        br.close();
    }

    public void evaluate(String trainFile, String testFile, String metricFile) throws Exception {
        BufferedReader trainReader = new BufferedReader(new FileReader(trainFile));
        Instances train = new Instances(trainReader);
        train.setClassIndex(train.numAttributes() -1);

        BufferedReader testReader = new BufferedReader(new FileReader(testFile));
        Instances test = new Instances(testReader);
        test.setClassIndex(test.numAttributes() -1);

        System.out.println("Start training model");
        long start = System.currentTimeMillis();
        classifier.buildClassifier(train);
        long end = System.currentTimeMillis();
        String time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(end - start));
        System.out.println("Finished training model. time used is " + time);

        System.out.println("Start testing dataset");
        start = System.currentTimeMillis();
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(classifier, test);
        end = System.currentTimeMillis();
        time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(end - start));
        System.out.println("Finished testing dataset. time used is " + time);

        StringBuilder sb = new StringBuilder();
        String summary= eval.toSummaryString();
        String details = eval.toClassDetailsString();
        String matrix = eval.toMatrixString();
        sb.append("=== Evaluation Results ===");
        sb.append(summary);
        sb.append(details);
        sb.append(matrix);
        String msg = sb.toString();
        System.out.println(msg);

        BufferedWriter bw = new BufferedWriter(new FileWriter(metricFile));
        bw.write(msg);
        bw.write('\n');
        bw.close();
    }

}
