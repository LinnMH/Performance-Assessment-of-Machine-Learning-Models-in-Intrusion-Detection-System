package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DataSelection {

    public static void select(String originalFileName, String trainFileName, String testFileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(originalFileName));
        BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainFileName));
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFileName));

        String line = br.readLine();
        if (line == null) {
            return;
        }

        List<String[]> lines = new ArrayList<>();
        String[] dimensions = line.split(",");

        while((line = br.readLine()) != null) {
            dimensions = line.split(",");
            lines.add(dimensions);
        }

        br.close();
        trainWriter.close();;
        testWriter.close();;
    }
}
