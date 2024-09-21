import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataPreprocessor {
    public static void main(String[] args) {
        String csvFile = "D:\\bowen\\CS-870\\week3\\MachineLearningCVE\\Friday-WorkingHours-Afternoon-DDos.pcap_ISCX.csv";  // 指定CSV文件路径
        String line = "";
        String csvSplitBy = ",";  // 逗号分隔符
        List<String[]> selectedData = new ArrayList<>();  // 存储选择的数据

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int rowLimit = 100;  // 选择前100行数据（可以根据需求修改）
            int rowCount = 0;

            // 按行读取文件
            while ((line = br.readLine()) != null) {
                // 使用逗号作为分隔符
                String[] dataRow = line.split(csvSplitBy);

                // 根据条件选择数据（此处为示例，选择前100行）
                if (rowCount < rowLimit) {
                    selectedData.add(dataRow);
                    rowCount++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 输出选择的数据
        for (String[] row : selectedData) {
            for (String field : row) {
                System.out.print(field + "\t");
            }
            System.out.println();
        }
    }
}
