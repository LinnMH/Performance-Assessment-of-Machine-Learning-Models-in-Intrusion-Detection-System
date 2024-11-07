package preprocessing;

import java.io.*;
import java.util.*;

public class DataSelection {

    private static Map<String, Integer> count(String file) throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        while((line = br.readLine()) != null) {
            String[] attributes = line.split(",");
            String label = attributes[attributes.length - 1];
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }
        br.close();
        return counts;
    }

    // return attributes and nominal values
    public static void select(String originalFile, String trainFile, String testFile,
                                                   String rule, boolean filterNaN, boolean writeTest) throws Exception {
        Map<String, Double> percents = new HashMap<>();
        for (String str : rule.split(";")) {
            String[] splits = str.split("=");
            String label = splits[0];
            double percent = Double.parseDouble(splits[1]);
            percents.put(label.toLowerCase(), percent);
        }

        Map<String, List<String>> attributes = new HashMap<>();
        Map<String, Integer> totalCounts = count(originalFile);
        Map<String, Integer> trainCounts = new HashMap<>();

        BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainFile));
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFile));

        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        String line = br.readLine();
        if (line == null) {
            return;
        }
        for (String attribute : line.split(",")) {
            attributes.put(attribute.trim(), new ArrayList<>());
        }
        trainWriter.write(line);
        testWriter.write(line);

        while((line = br.readLine()) != null) {
            if (filterNaN){
                if (line.toLowerCase().contains(",infinity,") || line.toLowerCase().contains(",nan,")) {
                    continue;
                }
            }
            String[] splits = line.split(",");
            String label = splits[splits.length -1];
            int totalCount = totalCounts.get(label);
            int trainCount = trainCounts.getOrDefault(label, 0);

            double percent = percents.getOrDefault(label.toLowerCase(),
                    percents.getOrDefault("default", 1.0));

            if ((double)trainCount < (double)totalCount * percent) {
                trainCounts.put(label, trainCount + 1);
                trainWriter.newLine();
                trainWriter.write(line);
            } else if (writeTest) {
                testWriter.newLine();
                testWriter.write(line);
            }
        }

        br.close();
        trainWriter.close();
        testWriter.close();
        showStat(totalCounts, trainCounts);
    }

    private static void showStat(Map<String, Integer>... counts) {
        for (Map<String, Integer> count : counts) {
            System.out.println("/////////////////////Stat/////////////////////");
            for (Map.Entry<String, Integer> entry : count.entrySet()) {
                System.out.println(entry);
            }
            System.out.println("//////////////////////////////////////////////");
        }
    }
}
