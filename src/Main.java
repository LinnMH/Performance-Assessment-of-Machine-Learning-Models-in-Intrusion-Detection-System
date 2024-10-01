import assessment.Metric;
import preprocessing.DataSelection;
import training.WekaModel;
import util.CSVUtil;

public class Main {

    public static void main(String[] args) throws Exception {

//        CSVUtil.combineFolder("D:\\csv\\ids2017",
//                "D:\\csv\\IDS2017_Combined.csv");
//        System.out.println("Finished combining csv files");
//
//        DataSelection.select("D:\\csv\\IDS2017_Combined.csv",
//                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_test.csv", 0.7);
//        System.out.println("Finished selecting data as training and testing dataset");
//
//        CSVUtil.modifyLabel("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_test_mod.csv", "?");
//        System.out.println("Finished modifying test csv");
//
//        CSVUtil.csvToArff("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train.arff");
//        System.out.println("Finished converting training csv to arff");
//
//        CSVUtil.csvToArff("D:\\csv\\IDS2017_test_mod.csv", "D:\\csv\\IDS2017_test_mod.arff");
//        System.out.println("Finished converting testing csv to arff");

        WekaModel model = new WekaModel("j48");
        model.train("D:\\csv\\IDS2017_train.arff");
        System.out.println("Finished training dataset");

        model.test("D:\\csv\\IDS2017_test_mod.arff", "D:\\csv\\IDS2017_predict.arff");
        System.out.println("Finished testing dataset");

        CSVUtil.arffToCsv("D:\\csv\\IDS2017_predict.arff", "D:\\csv\\IDS2017_predict.csv");
        System.out.println("Finished converting predict arff to csv");

        Metric.evaluate("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_predict.csv",
                "D:\\csv\\IDS2017_metric.txt");
        System.out.println("Finished evaluating");

    }
}
