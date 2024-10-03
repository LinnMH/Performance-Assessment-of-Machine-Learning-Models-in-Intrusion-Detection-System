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
    public static Map<String, List<String>> select(String originalFile, String trainFile, String testFile, double percent) throws Exception {
        Map<String, List<String>> attributes = new HashMap<>();
        Map<String, Integer> totalCounts = count(originalFile);
        Map<String, Integer> trainCounts = new HashMap<>();

        BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainFile));
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFile));

        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        for (String attribute : line.split(",")) {
            attributes.put(attribute.trim(), new ArrayList<>());
        }
        trainWriter.write(line);
        testWriter.write(line);

        while((line = br.readLine()) != null) {
            String[] splits = line.split(",");
            String label = splits[splits.length -1];
            int totalCount = totalCounts.get(label);
            int trainCount = trainCounts.getOrDefault(label, 0);

            if ((double)trainCount < (double)totalCount * percent) {
                trainCounts.put(label, trainCount + 1);
                trainWriter.newLine();
                trainWriter.write(line);
            } else {
                testWriter.newLine();
                testWriter.write(line);
            }
        }

        br.close();
        trainWriter.close();
        testWriter.close();

//        showStat(totalCounts, trainCounts);

        attributes.get("Label").addAll(totalCounts.keySet());
        return attributes;
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
