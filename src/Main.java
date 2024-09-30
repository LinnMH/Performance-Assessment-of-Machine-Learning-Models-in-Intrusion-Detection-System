import assessment.Metric;
import preprocessing.DataSelection;
import training.WekaModel;
import util.CSVUtil;

public class Main {

    public static void main(String[] args) throws Exception {

        CSVUtil.combineFolder("D:\\csv\\ids2017",
                "D:\\csv\\IDS2017_Combined.csv");

        DataSelection.select("D:\\csv\\IDS2017_Combined.csv",
                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_test.csv");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train.arff");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_test.arff");

        WekaModel model = new WekaModel("naiivebayes");
        model.train("D:\\csv\\IDS2017_train.arff");
        model.test("D:\\csv\\IDS2017_test.arff", "D:\\csv\\IDS2017_predict.arff");

        Metric.evaluate("IDS2017_test.arff", "D:\\csv\\IDS2017_predict.arff",
                "D:\\csv\\IDS2017_metric.txt");

    }
}
