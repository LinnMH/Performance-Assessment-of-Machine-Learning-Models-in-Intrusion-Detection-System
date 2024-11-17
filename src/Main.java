import preprocessing.DataFilter;
import preprocessing.DataGroup;
import preprocessing.DataSelection;
import training.WekaModel;
import util.CSVUtil;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
//        evalNB15By2017();
//        evalNB15Standalone();
        eval2018By2017();
//        eval2018Standalone();
//        eval2018ByNB15();
//        eval2017Standalone();
    }

    public static void evalNB15By2017() throws Exception {
        CSVUtil.combineFolder("D:\\csv\\ids2017","D:\\csv\\IDS2017_Combined.csv");
        System.out.println("Finished combining csv files");

        DataGroup.group("D:\\csv\\IDS2017_Combined.csv", "D:\\csv\\IDS2017_grouped.csv",
                "ids2017to2018");

        DataSelection.select("D:\\csv\\IDS2017_grouped.csv",
                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\empty.csv",
                "default=0.2", false, false);
        System.out.println("Finished selecting data as training and testing dataset");

        DataFilter.filterCol("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train_filtered.csv", new int[]{55}, true);
        System.out.println("Finished filtering train data");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\IDS2017_train_filtered.csv");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_train_filtered.csv", "D:\\csv\\IDS2017_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");

        DataFilter.filterCol("D:\\csv\\nb15\\CICFlowMeter_out.csv", "D:\\csv\\nb15_test_filtered.csv",
                new int[]{0, 1, 2, 3, 5, 6}, true);
        System.out.println("Finished filtering test data");

        DataFilter.replaceHeaderLabel("D:\\csv\\nb15_test_filtered.csv", "D:\\csv\\nb15_test_replaced.csv",
                "D:\\csv\\IDS2017_train_filtered.csv",
                "Benign=Benign;DoS=DoS;Reconnaissance=PortScan;Exploits=Bot;Backdoor=Bot;Shellcode=Bot;" +
                        "Worms=Web Attack;Fuzzers=Web Attack;Analysis=Web Attack;Generic=Web Attack");
        System.out.println("Finished replacing test data header label");

        CSVUtil.csvToArff("D:\\csv\\nb15_test_replaced.csv", "D:\\csv\\nb15_test.arff", attributes);
        System.out.println("Finished converting testing csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\IDS2017_train.arff", "D:\\csv\\nb15_test.arff", "D:\\csv\\nb15_metric.txt");

    }

    public static void eval2018By2017() throws Exception {
        CSVUtil.combineFolder("D:\\csv\\ids2017", "D:\\csv\\IDS2017_Combined.csv");
        System.out.println("Finished combining csv files");

        DataGroup.group("D:\\csv\\IDS2017_Combined.csv", "D:\\csv\\IDS2017_grouped.csv",
                "ids2017to2018");

        DataSelection.select("D:\\csv\\IDS2017_grouped.csv",
                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\empty.csv",
                "default=0.2;bot=0;web attack=0;portscan=0;bruteforce=0", false, false);
        System.out.println("Finished selecting data as training and testing dataset");

        DataFilter.filterCol("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train_filtered.csv", new int[]{55}, true);
        System.out.println("Finished filtering train data");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\IDS2017_train_filtered.csv");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_train_filtered.csv", "D:\\csv\\IDS2017_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");


        CSVUtil.combineFolder("D:\\csv\\a", "D:\\csv\\IDS2018_Combined.csv");
        System.out.println("Finished combining csv files");

        DataFilter.filterCol("D:\\csv\\IDS2018_Combined.csv", "D:\\csv\\IDS2018_test_filtered.csv", new int[]{1, 2}, true);
        System.out.println("Finished filtering test data");

        DataFilter.replaceHeaderLabel("D:\\csv\\IDS2018_test_filtered.csv", "D:\\csv\\IDS2018_test_replaced.csv",
                "D:\\csv\\IDS2017_train_filtered.csv",
                "Benign=Benign;FTP-BruteForce=BruteForce;SSH-Bruteforce=BruteForce;Brute Force -XSS=BruteForce;" +
                        "Brute Force -Web=BruteForce;DDOS attack-HOIC=DoS;DDOS attack-LOIC-UDP=DoS;DDoS attacks-LOIC-HTTP=DoS;" +
                        "DoS attacks-GoldenEye=DoS;DoS attacks-Hulk=DoS;DoS attacks-SlowHTTPTest=DoS;DoS attacks-Slowloris=DoS;" +
                        "SQL Injection=Web Attack;Infilteration=PortScan;Bot=Bot;");
        System.out.println("Finished replacing test data header label");


        CSVUtil.csvToArff("D:\\csv\\IDS2018_test_replaced.csv", "D:\\csv\\IDS2018_test.arff", attributes);
        System.out.println("Finished converting testing csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\IDS2017_train.arff", "D:\\csv\\IDS2018_test.arff", "D:\\csv\\IDS2018_metric.txt");
    }

    public static void eval2017Standalone() throws Exception {
        CSVUtil.combineFolder("D:\\csv\\ids2017",
                "D:\\csv\\IDS2017_Combined.csv");
        System.out.println("Finished combining csv files");

        DataSelection.select("D:\\csv\\IDS2017_Combined.csv",
                "D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_test.csv",
                "default=0.2", false, true);
        System.out.println("Finished selecting data as training and testing dataset");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\IDS2017_train.csv");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_train.csv", "D:\\csv\\IDS2017_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");

        CSVUtil.csvToArff("D:\\csv\\IDS2017_test.csv", "D:\\csv\\IDS2017_test.arff", attributes);
        System.out.println("Finished converting test csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\IDS2017_train.arff", "D:\\csv\\IDS2017_test.arff", "D:\\csv\\IDS2017_metric.txt");
    }

    public static void evalNB15Standalone() throws Exception {
        DataFilter.filterCol("D:\\csv\\nb15\\CICFlowMeter_out.csv", "D:\\csv\\nb15_filtered.csv",
                new int[]{0, 1, 2, 3, 5, 6}, true);
        System.out.println("Finished filtering test data");

        DataSelection.select("D:\\csv\\nb15_filtered.csv",
                "D:\\csv\\nb15_train.csv", "D:\\csv\\nb15_test.csv",
                "default=0.2", false, true);
        System.out.println("Finished selecting data as training and testing dataset");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\nb15_train.csv");

        CSVUtil.csvToArff("D:\\csv\\nb15_train.csv", "D:\\csv\\nb15_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");

        CSVUtil.csvToArff("D:\\csv\\nb15_test.csv", "D:\\csv\\nb15_test.arff", attributes);
        System.out.println("Finished converting test csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\nb15_train.arff", "D:\\csv\\nb15_test.arff", "D:\\csv\\nb15_metric.txt");
    }

    public static void eval2018Standalone() throws Exception {
        CSVUtil.combineFolder("D:\\csv\\ids2018",
                "D:\\csv\\IDS2018_Combined.csv");
        System.out.println("Finished combining csv files");

        DataFilter.filterCol("D:\\csv\\IDS2018_Combined.csv", "D:\\csv\\IDS2018_filtered.csv", new int[]{1, 2}, true);
        System.out.println("Finished filtering test data");

        DataSelection.select("D:\\csv\\IDS2018_filtered.csv",
                "D:\\csv\\IDS2018_train.csv", "D:\\csv\\IDS2018_test.csv",
                "default=0.1", false, true);
        System.out.println("Finished selecting data as training and testing dataset");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\IDS2018_train.csv");

        CSVUtil.csvToArff("D:\\csv\\IDS2018_train.csv", "D:\\csv\\IDS2018_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");

        CSVUtil.csvToArff("D:\\csv\\IDS2018_test.csv", "D:\\csv\\IDS2018_test.arff", attributes);
        System.out.println("Finished converting test csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\IDS2018_train.arff", "D:\\csv\\IDS2018_test.arff", "D:\\csv\\IDS2018_metric.txt");
    }


    public static void eval2018ByNB15() throws Exception {
        DataFilter.filterCol("D:\\csv\\nb15\\CICFlowMeter_out.csv", "D:\\csv\\nb15_filtered.csv",
                new int[]{0, 1, 2, 3, 5, 6}, true);
        System.out.println("Finished filtering test data");

        DataSelection.select("D:\\csv\\nb15_filtered.csv",
                "D:\\csv\\nb15_train.csv", "D:\\csv\\nb15_test.csv",
                "default=0.2", false, false);
        System.out.println("Finished selecting data as training and testing dataset");

        DataFilter.replaceHeaderLabel("D:\\csv\\nb15_train.csv", "D:\\csv\\nb15_replaced.csv",null,
                "Benign=Benign;DoS=Attack;Reconnaissance=Attack;Exploits=Attack;Backdoor=Attack;Shellcode=Attack;" +
                        "Worms=Attack;Fuzzers=Attack;Analysis=Attack;Generic=Attack");
        System.out.println("Finished replacing test data header label");

        Map<String, List<String>> attributes = CSVUtil.getAttributes("D:\\csv\\nb15_replaced.csv");

        CSVUtil.csvToArff("D:\\csv\\nb15_replaced.csv", "D:\\csv\\nb15_train.arff", attributes);
        System.out.println("Finished converting training csv to arff");


        CSVUtil.combineFolder("D:\\csv\\ids2018", "D:\\csv\\IDS2018_Combined.csv");
        System.out.println("Finished combining csv files");

        DataFilter.filterCol("D:\\csv\\IDS2018_Combined.csv", "D:\\csv\\IDS2018_filtered.csv", new int[]{1, 2}, true);
        System.out.println("Finished filtering test data");

        DataFilter.replaceHeaderLabel("D:\\csv\\IDS2018_filtered.csv", "D:\\csv\\IDS2018_replaced.csv",
                "D:\\csv\\nb15_replaced.csv",
                "Benign=Benign;FTP-BruteForce=Attack;SSH-Bruteforce=Attack;Brute Force -XSS=Attack;" +
                        "Brute Force -Web=Attack;DDOS attack-HOIC=Attack;DDOS attack-LOIC-UDP=Attack;DDoS attacks-LOIC-HTTP=Attack;" +
                        "DoS attacks-GoldenEye=Attack;DoS attacks-Hulk=Attack;DoS attacks-SlowHTTPTest=Attack;DoS attacks-Slowloris=Attack;" +
                        "SQL Injection=Attack;Infilteration=Attack;Bot=Attack;");
        System.out.println("Finished replacing test data header label");

        CSVUtil.csvToArff("D:\\csv\\IDS2018_replaced.csv", "D:\\csv\\IDS2018_test.arff", attributes);
        System.out.println("Finished converting test csv to arff");

        WekaModel model = new WekaModel("j48");
        model.evaluate("D:\\csv\\nb15_train.arff", "D:\\csv\\IDS2018_test.arff", "D:\\csv\\IDS2018_metric.txt");
    }


}
