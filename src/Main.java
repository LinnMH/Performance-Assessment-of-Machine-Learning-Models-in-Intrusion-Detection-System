import assessment.Metric;
import preprocessing.DataFilter;
import preprocessing.DataGroup;
import preprocessing.DataSelection;
import training.WekaModel;
import util.CSVUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

//        CSVUtil.combineFolder("D:\\csv\\ids2017",
//                "D:\\csv\\IDS2017_Combined.csv");
//        System.out.println("Finished combining csv files");

//        DataGroup.group("D:\\csv\\IDS2017_Combined.csv", "D:\\csv\\IDS2017_grouped.csv",
//                "ids2017");

//        Map<String, List<String>> labels = DataSelection.select("D:\\csv\\IDS2017_Combined.csv",
//                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_test.csv",
//                "default=0.2", false, false);
//        System.out.println("Finished selecting data as training and testing dataset");
//
//        DataFilter.filterCol("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train_filtered.csv", new int[]{55}, true);
//        System.out.println("Finished filtering train data");
//
//        DataFilter.filterCol("D:\\csv\\IDS2018\\02-14-2018.csv", "D:\\csv\\IDS2018_test_filtered.csv", new int[]{1,2}, true);
//        System.out.println("Finished filtering test data");
//
//        DataFilter.replaceHeaderLabel("D:\\csv\\IDS2018_test_filtered.csv", "D:\\csv\\IDS2018_test_replaced.csv",
//                "D:\\csv\\IDS2017_train_filtered.csv",
//                "Benign=BENIGN;FTP-BruteForce=FTP-Patator;SSH-Bruteforce=SSH-Patator");
//        System.out.println("Finished replacing header and label of test data");
//
//        CSVUtil.csvToArff("D:\\csv\\IDS2017_train_filtered.csv", "D:\\csv\\IDS2017_train.arff", labels);
//        System.out.println("Finished converting training csv to arff");
//
//        CSVUtil.csvToArff("D:\\csv\\IDS2018_test_replaced.csv", "D:\\csv\\IDS2018_test.arff", labels);
//        System.out.println("Finished converting testing csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\IDS2017_train.arff", "D:\\csv\\IDS2018_test.arff", "D:\\csv\\IDS2017_metric.txt");

//        System.out.println("Start training model with training dataset");
//        long start = System.currentTimeMillis();
//        WekaModel model = new WekaModel("j48");
//        model.train("D:\\csv\\IDS2017_train.arff");
//        long end = System.currentTimeMillis();
//        String time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(end - start));
//        System.out.println("Finished training dataset. time used is " + time);
//
//        System.out.println("Start testing dataset with model");
//        start = System.currentTimeMillis();
//        model.test("D:\\csv\\IDS2017_test.arff", "D:\\csv\\IDS2017_predict.arff");
//        end = System.currentTimeMillis();
//        time = (new SimpleDateFormat("mm:ss:SSS")).format(new Date(end - start));
//        System.out.println("Finished testing dataset. time used is " + time);
//
//        CSVUtil.arffToCsv("D:\\csv\\IDS2017_predict.arff", "D:\\csv\\IDS2017_predict.csv");
//        System.out.println("Finished converting predict arff to csv");

//        Metric.evaluateBinary("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_predict.csv",
//                "D:\\csv\\IDS2017_metric.txt");
//        System.out.println("Finished evaluating");

    }


}
