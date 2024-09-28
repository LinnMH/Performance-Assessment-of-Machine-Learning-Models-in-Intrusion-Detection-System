import preprocessing.WekaModel;

public class Main {

    public static void main(String[] args) throws Exception {

        WekaModel model = new WekaModel("naiivebayes");

        String train = model.train("weather.numeric.arff");
        System.out.println(train);

        String test = model.test("weather.numeric-test.arff");
        System.out.println(test);
    }
}
