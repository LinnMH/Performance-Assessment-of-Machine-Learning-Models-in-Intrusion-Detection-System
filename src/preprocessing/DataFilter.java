package preprocessing;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class DataFilter {

    public static void filterCol(String srcFile, String dstFile, int[] columnsToRemove, boolean filterNanInf) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(srcFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(dstFile));

        String line;
        while ((line = br.readLine()) != null) {
            if (filterNanInf & line.toLowerCase().contains(",nan,")) {
                continue;
            }
            if (filterNanInf & line.toLowerCase().contains(",inf,")) {
                continue;
            }

            String[] columns = line.split(",");

            List<String> filteredColumns = new ArrayList<>();
            for (int i = 0; i < columns.length; i++) {
                if (Arrays.binarySearch(columnsToRemove, i) < 0) {
                    filteredColumns.add(columns[i]);
                }
            }

            bw.write(String.join(",", filteredColumns));
            bw.newLine();
        }
        br.close();
        bw.close();
    }

    public static void replaceHeaderLabel(String srcFile, String dstFile, String headerFile, String rule) throws IOException {
        Map<String, String> labelMapping = new HashMap<>();
        for (String mapping : rule.split(";")) {
            String[] v = mapping.split("=");
            labelMapping.put(v[0], v[1]);
        }

        BufferedReader br = new BufferedReader(new FileReader(srcFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(dstFile));
        String line = br.readLine(); // skip header
        if (headerFile != null) {
            BufferedReader headReader = new BufferedReader(new FileReader(headerFile));
            line = headReader.readLine();
            headReader.close();
        }
        bw.write(line);
        bw.newLine();
        while ((line = br.readLine()) != null) {
            // Split the line into columns
            String[] columns = line.split(",");

            String key = columns[columns.length - 1];
            if (labelMapping.containsKey(key)) {
                columns[columns.length - 1] = labelMapping.get(key);
            }

            bw.write(String.join(",", columns));
            bw.newLine();
        }
        br.close();
        bw.close();
    }

}
