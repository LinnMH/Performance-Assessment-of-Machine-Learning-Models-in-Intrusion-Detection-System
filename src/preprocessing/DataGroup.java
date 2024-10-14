package preprocessing;

import java.io.*;
public class DataGroup {

    public static void group(String srcFile, String dstFile, String rule) throws IOException {
        BufferedWriter groupWriter = new BufferedWriter(new FileWriter(dstFile));
        BufferedReader br = new BufferedReader(new FileReader(srcFile));
        String line = br.readLine();
        if (line == null) {
            return;
        }
        groupWriter.write(line);

        while((line = br.readLine()) != null) {
            String[] splits = line.split(",");
            String label = splits[splits.length -1];

            if (rule.equalsIgnoreCase("ids2017")) {
                splits[splits.length -1] = getIDS2017Label(label);
            } else if (rule.equalsIgnoreCase("binary")) {
                splits[splits.length -1] = getBinaryLabel(label);
            }

            line = String.join(",", splits);
            groupWriter.newLine();
            groupWriter.write(line);
        }

        groupWriter.close();
        br.close();
    }

    private static String getIDS2017Label(String label) {
        label = label.toLowerCase();
        if (label.contains("bot")) {
            return "bot";
        } else if (label.contains("web attack")) {
            return "web attack";
        } else if (label.contains("dos")) {
            return "dos";
        } else if (label.contains("port scan")) {
            return "probe";
        } else if (label.contains("patator") || label.contains("heartbleed")) {
            return "r2l";
        } else if (label.contains("infiltration")) {
            return "infiltration";
        }
        return label;
    }

    private static String getBinaryLabel(String label) {
        label = label.toLowerCase();
        if (label.contains("benign")) {
            return "benign";
        }
        return "attack";
    }
}
