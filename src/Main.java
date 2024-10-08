import assessment.Metric;
import preprocessing.DataSelection;
import training.WekaModel;
import util.CSVUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        CSVUtil.combineFolder("D:\\csv\\ids2017",
                "D:\\csv\\IDS2017_Combined.csv");
        System.out.println("Finished combining csv files");

        Map<String, List<String>> labels = DataSelection.select("D:\\csv\\IDS2017_Combined.csv",
                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_test.csv", 0.7);
        System.out.println("Finished selecting data as training and testing dataset");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train.arff", labels);
        System.out.println("Finished converting training csv to arff");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_test.arff", labels);
        System.out.println("Finished converting testing csv to arff");

        CSVUtil.removeLabel("D:\\csv\\IDS2017_test.arff", "D:\\csv\\IDS2017_test_nolabel.arff",
                "D:\\csv\\IDS2017_train.arff");
        System.out.println("Finished removing label from testing arff");

        System.out.println("Start training model with training dataset");
        long start = System.currentTimeMillis();
        WekaModel model = new WekaModel("j48");
        model.train("D:\\csv\\IDS2017_train.arff");
        long end = System.currentTimeMillis();
        String time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(end - start));
        System.out.println("Finished training dataset. time used is " + time);

        System.out.println("Start testing dataset with model");
        model.test("D:\\csv\\IDS2017_test_nolabel.arff", "D:\\csv\\IDS2017_predict.arff");
        System.out.println("Finished testing dataset");

        CSVUtil.arffToCsv("D:\\csv\\IDS2017_predict.arff", "D:\\csv\\IDS2017_predict.csv");
        System.out.println("Finished converting predict arff to csv");

        Metric.evaluate("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_predict.csv",
                "D:\\csv\\IDS2017_metric.txt");
        System.out.println("Finished evaluating");

    }
}
