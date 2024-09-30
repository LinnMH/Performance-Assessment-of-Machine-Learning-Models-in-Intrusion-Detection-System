package training;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class WekaModel {

    private String modelName;

    private Classifier classifier = null;

    public WekaModel(String modelName) {
        this.modelName = modelName;
        if (modelName.equalsIgnoreCase("j48")) {
            classifier = new J48();
        } else if (modelName.equalsIgnoreCase("svm")) {
//            classifier = new SupportVectorMachineModel();
        } else if (modelName.equalsIgnoreCase("mlp")) {
            classifier = new MultilayerPerceptron();
        } else if (modelName.equalsIgnoreCase("naiivebayes")) {
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

    public void test(String testFile, String targetFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        Instances test = new Instances(br);
        test.setClassIndex(test.numAttributes() -1);
        for (int i = 0; i < test.size(); i++) {
            double label = classifier.classifyInstance(test.instance(i));
            test.instance(i).setClassValue(label);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
        bw.write(test.toString());
        bw.close();
        br.close();
    }
}
