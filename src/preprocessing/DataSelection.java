package preprocessing;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void select(String originalFile, String trainFile, String testFile, double percent) throws Exception {
        Map<String, Integer> totalCounts = count(originalFile);
        Map<String, Integer> trainCounts = new HashMap<>(totalCounts.size());

        BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainFile));
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFile));

        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        String line = br.readLine();
        if (line == null) {
            return;
        }

        trainWriter.write(line);
        testWriter.write(line);

        while((line = br.readLine()) != null) {
            String[] attributes = line.split(",");
            String label = attributes[attributes.length -1];
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
        trainWriter.close();;
        testWriter.close();;
    }
}
