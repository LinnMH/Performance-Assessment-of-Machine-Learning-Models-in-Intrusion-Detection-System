package util;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
//
//    public static void loadFolder(String folderName) throws Exception {
//        List<String> fileNames = listFiles(folderName);
//        for (String fileName : fileNames) {
//            loadFile(fileName);
//        }
//    }
//    private static void loadFile(String fileName) throws Exception{
//        BufferedReader br = new BufferedReader(new FileReader(fileName));
//        String line = br.readLine();
//        if (line == null) {
//            return;
//        }
//        List<String[]> lines = new ArrayList<>();
//        String[] dimensions = line.split(",");
//
//        while((line = br.readLine()) != null) {
//            dimensions = line.split(",");
//            lines.add(dimensions);
//        }
//
//        br.close();
//    }

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

    public static void csvToArff(String srcFile, String dstFile) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(srcFile));
        Instances data = loader.getDataSet();

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

    public static void modifyLabel(String srcFile, String dstFile, String substitute) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(dstFile));
        BufferedReader br = new BufferedReader(new FileReader(srcFile));
        String line = br.readLine();  // first line is dimension names
        bw.write(line);
        while ((line = br.readLine()) != null) {
            String[] splits = line.split(",");
            splits[splits.length - 1] = substitute;
            line = String.join(",", splits);
            bw.newLine();
            bw.write(line);
        }
        br.close();
        bw.close();
    }
}
