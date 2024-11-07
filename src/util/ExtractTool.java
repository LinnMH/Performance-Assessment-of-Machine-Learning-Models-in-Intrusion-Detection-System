package util;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractTool {

    public static void main(String[] args) throws IOException {
        List<String> fileNames = CSVUtil.listFiles("D:\\csv\\nb15");
        Set<String> labels = new HashSet<>();

        for (String fileName : fileNames) {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                String[] split = line.split(",");
                labels.add(split[split.length - 1]);
            }
            br.close();
        }

        String[] array = labels.toArray(new String[0]);
        Arrays.sort(array);
        BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\csv\\nb15_labels.csv"));
        String result = String.join(",", array);
        bw.write(result);
        bw.close();;
        System.out.println(labels);
    }
}
