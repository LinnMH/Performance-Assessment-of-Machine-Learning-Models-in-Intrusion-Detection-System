import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Directory2023CSVProcessor {

    public static void main(String[] args) {
        // 设置你的CSV文件所在的目录路径
        String directoryPath = "D:\\bowen\\CS-870\\week3\\CIC IoT Dataset 2023\\archive";  // 替换为实际的路径
        File folder = new File(directoryPath);

        Map<String, Integer> attackCount = new HashMap<>();
        int[] benignCount = {0};  // 使用数组来保持可变性，确保可以在方法中正确更新
        int[] attackTotal = {0};  // 用于二元分类中的总攻击数

        // 处理目录中的每个CSV文件
        if (folder.exists() && folder.isDirectory()) {
            for (File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile() && fileEntry.getName().endsWith(".csv")) {
                    processCSVFile(fileEntry, attackCount, benignCount, attackTotal);
                }
            }
        } else {
            System.out.println("Invalid directory path: " + directoryPath);
            return;
        }

        // 计算总流量
        int totalTraffic = benignCount[0] + attackTotal[0];

        // 输出过滤后的攻击类型统计，过滤掉数量为1的攻击类型
        System.out.println("\n--- Filtered Attack Count ---");
        System.out.printf("%-20s %-35s %20s%n", "Category", "Type", "Number of Instances");
        System.out.println("-------------------------------------------------------------");

        for (Map.Entry<String, Integer> entry : attackCount.entrySet()) {
            if (entry.getValue() > 1) {  // 过滤掉数量为1的攻击类型
                System.out.printf("%-20s %-35s %20d%n", "attack", entry.getKey(), entry.getValue());
            }
        }

        // 输出二元分类表格
        System.out.println("\n--- Binary Classification Table ---");
        System.out.printf("%-20s %20s %25s%n", "Category", "Number of Instances", "Proportion");
        System.out.println("--------------------------------------------------------------");

        // 输出BENIGN的统计
        double benignProportion = (double) benignCount[0] / totalTraffic * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "BENIGN", benignCount[0], benignProportion);

        // 输出ATTACK的统计
        double attackProportion = (double) attackTotal[0] / totalTraffic * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "ATTACK", attackTotal[0], attackProportion);

        // 输出按类别分类的攻击统计表
        System.out.println("\n--- Categorized Attack Table ---");
        System.out.printf("%-20s %35s %20s%n", "Category", "Attack Type", "Number of Instances");
        System.out.println("---------------------------------------------------------------------");

        // 遍历攻击类型，输出按类别分类的表格，并过滤掉数量为1的攻击
        Map<String, Map<String, Integer>> categorizedAttackCount = categorizeAttacks(attackCount);

        for (Map.Entry<String, Map<String, Integer>> categoryEntry : categorizedAttackCount.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Integer> attacks = categoryEntry.getValue();
            int totalCategoryCount = attacks.values().stream().mapToInt(Integer::intValue).sum();

            // 输出攻击类别的总数量
            if (totalCategoryCount > 1) {  // 过滤掉数量为1的类别
                System.out.printf("%-20s %35s %20d%n", category, category, totalCategoryCount);

                // 输出该类别下的每种具体攻击类型
                for (Map.Entry<String, Integer> attackEntry : attacks.entrySet()) {
                    if (attackEntry.getValue() > 1) {  // 过滤掉数量为1的具体攻击
                        System.out.printf("%-20s %35s %20d%n", "", attackEntry.getKey(), attackEntry.getValue());
                    }
                }
            }
        }
    }

    // 处理单个CSV文件并统计每种攻击类型和benign数量
    public static void processCSVFile(File csvFile, Map<String, Integer> attackCount, int[] benignCount, int[] attackTotal) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine();  // 跳过文件头

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;  // 跳过空行
                }

                String[] dataRow = line.split(csvSplitBy);
                int labelIndex = dataRow.length - 1;  // 最后一列（表示是否为攻击）

                if (labelIndex < 0) {
                    continue;  // 跳过格式不正确的行
                }

                String label = dataRow[labelIndex].trim();  // 获取最后一列的值

                if (label.equalsIgnoreCase("BenignTraffic")) {  // 如果最后一列为BenignTraffic，表示为benign
                    benignCount[0]++;
                } else {  // 否则为攻击类型
                    attackCount.put(label, attackCount.getOrDefault(label, 0) + 1);
                    attackTotal[0]++;  // 累计总攻击数
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将攻击按类别分类
    public static Map<String, Map<String, Integer>> categorizeAttacks(Map<String, Integer> attackCount) {
        Map<String, Map<String, Integer>> categorized = new HashMap<>();

        for (Map.Entry<String, Integer> entry : attackCount.entrySet()) {
            String attackType = entry.getKey();
            int count = entry.getValue();

            // 使用映射进行简单的分类（可以根据需求进一步细分）
            String category;
            if (attackType.contains("Web Attack")) {
                category = "Web Attack";
            } else if (attackType.contains("DoS")) {
                category = "DOS";
            } else if (attackType.contains("Reconnaissance")) {
                category = "PROBE";
            } else if (attackType.contains("Patator") || attackType.contains("Heartbleed")) {
                category = "R2L";
            } else {
                category = "Other";
            }

            // 更新分类映射
            categorized.putIfAbsent(category, new HashMap<>());
            categorized.get(category).put(attackType, count);
        }

        return categorized;
    }
}
