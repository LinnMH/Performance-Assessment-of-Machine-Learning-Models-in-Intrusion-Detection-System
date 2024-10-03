package assessment;

import java.io.*;

public class Metric {

    public static void evaluate(String testFile, String predictFile, String metricFile) throws IOException {
        BufferedReader testReader = new BufferedReader(new FileReader(testFile));
        BufferedReader predictReader = new BufferedReader(new FileReader(predictFile));

        // skip first line because they are definitions
        String testLine = testReader.readLine();
        String predictLine = predictReader.readLine();

        double tp = 0;
        double tn = 0;
        double fp = 0;
        double fn = 0;

        while (testLine != null && predictLine != null) {
            String[] testSplits = testLine.split(",");
            String[] predictSplits = predictLine.split(",");
            String testLabel = testSplits[testSplits.length - 1];
            String predictLabel = predictSplits[predictSplits.length - 1];

            // Prediction is correct
            if (testLabel.equalsIgnoreCase(predictLabel)) {
                if (testLabel.equalsIgnoreCase("benign")) {
                    tn++;
                } else {
                    tp++;
                }
            // Prediction is wrong
            } else {
                if (testLabel.equalsIgnoreCase("benign")) {
                    fn++;
                } else {
                    fp++;
                }
            }
            testLine = testReader.readLine();
            predictLine = predictReader.readLine();
        }

        if ((testLine != null && !testLine.isEmpty()) || (predictLine != null && !predictFile.isEmpty())) {
            System.err.println("different instance size between test file and predict file");
            return;
        }

        double fpr = fp / (tn + fp);
        double fnr = fn / (tp + fn);
        double recall = tp / (tp + fn);
        double precision = tp / (tp + fp);
        double f1score = 2.0 * precision * recall / (precision + recall);

        StringBuilder sb = new StringBuilder();
        sb.append("True Positive: " + tp + "\n");
        sb.append("True Negative: " + tn + "\n");
        sb.append("False Positive: " + fp + "\n");
        sb.append("False Negative: " + fn + "\n");
        sb.append("False Positive Rate: " + fpr + "\n");
        sb.append("False Negative Rate: " + fnr + "\n");
        sb.append("Precision: " + precision + "\n");
        sb.append("Recall: " + recall + "\n");
        sb.append("F1 score: " + f1score + "\n");

        testReader.close();
        predictReader.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(metricFile));
        bw.write(sb.toString());
        bw.close();
        System.out.println(sb);
    }

}
