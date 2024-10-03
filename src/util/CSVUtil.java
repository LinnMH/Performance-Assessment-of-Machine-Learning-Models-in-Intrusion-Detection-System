package util;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.unsupervised.attribute.SortLabels;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVUtil {

    private static List<String> listFiles(String folderName) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderName);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
            } else if (fileEntry.getName().contains(".csv")) {
                fileNames.add(folderName + "\\" + fileEntry.getName());
            }
        }
        return fileNames;
    }

    public static void combineFolder(String folderName, String targetFileName) throws Exception {
        List<String> fileNames = listFiles(folderName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetFileName));
        String attributes = null;
        for (String fileName : fileNames) {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();  // first line is dimension names
            if (line == null) {
                return;
            }
            if (attributes == null) {
                line = line.replaceFirst("Fwd Header Length", "Fwd Header Length 2");
                attributes = line;
                bw.write(attributes);
            }
            while ((line = br.readLine()) != null) {
                bw.newLine();
                bw.write(line);
            }
            br.close();
        }
        bw.close();
    }

    public static void csvToArff(String srcFile, String dstFile, Map<String, List<String>> attributes) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(srcFile));
        String[] nominalVals = { attributes.size() + ":" + String.join(",", attributes.get("Label")) };
        System.out.println(String.join("\n", nominalVals));
        loader.setNominalLabelSpecs(nominalVals);
        Instances data = loader.getDataSet();

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(dstFile));
        saver.writeBatch();
    }

    public static void removeLabel(String srcFile, String dstFile, String trainFile) throws IOException {
        ArffLoader loader = new ArffLoader();
        loader.setSource(new File(srcFile));
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() -1);
        for (int i = 0; i < data.size(); i++) {
            data.instance(i).setClassMissing();
        }
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(dstFile));
        saver.writeBatch();
    }

    public static void arffToCsv(String srcFile, String dstFile) throws IOException {
        ArffLoader loader = new ArffLoader();
        loader.setSource(new File(srcFile));
        Instances data = loader.getDataSet();

        CSVSaver saver = new CSVSaver();
        saver.setInstances(data);
        saver.setFile(new File(dstFile));
        saver.writeBatch();
    }

}
