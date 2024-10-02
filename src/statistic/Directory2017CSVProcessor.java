import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Directory2017CSVProcessor {

    // Mapping of attack types to categories (DOS, R2L, U2R, PROBE, etc.)
    private static final Map<String, String> attackCategoryMap = new HashMap<>();

    static {
        // Define mapping of each attack to its corresponding category
        attackCategoryMap.put("Web Attack � Sql Injection", "Web Attack");
        attackCategoryMap.put("Web Attack � XSS", "Web Attack");
        attackCategoryMap.put("Bot", "Bot");
        attackCategoryMap.put("SSH-Patator", "R2L");
        attackCategoryMap.put("FTP-Patator", "R2L");
        attackCategoryMap.put("DoS slowloris", "DOS");
        attackCategoryMap.put("DoS Hulk", "DOS");
        attackCategoryMap.put("DoS Slowhttptest", "DOS");
        attackCategoryMap.put("PortScan", "PROBE");
        attackCategoryMap.put("Infiltration", "Infiltration");
        attackCategoryMap.put("DDoS", "DOS");
        attackCategoryMap.put("Web Attack � Brute Force", "Web Attack");
        attackCategoryMap.put("DoS GoldenEye", "DOS");
        attackCategoryMap.put("Heartbleed", "R2L");
    }

    public static void main(String[] args) {
        String directoryPath = "D:\\bowen\\CS-870\\week3\\MachineLearningCVE";  // Specify the directory path containing CSV files
        File folder = new File(directoryPath);
        Map<String, Integer> globalTrafficCount = new HashMap<>();  // Global statistics map for all files
        Map<String, Map<String, Integer>> categoryCount = new HashMap<>();  // Map to store counts by category and individual attack types
        int totalBenignCount = 0;  // Variable to store the global benign count
        int totalAttackCount = 0;  // Variable to store the global attack count
        int totalInstances = 0;  // Variable to store the total number of instances

        // Process all CSV files in the directory
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.getName().endsWith(".csv")) {
                processCSVFile(fileEntry, globalTrafficCount, categoryCount);
            }
        }

        // Calculate the total number of instances and the global benign and attack counts
        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            totalInstances += entry.getValue();
            if (entry.getKey().equalsIgnoreCase("BENIGN")) {
                totalBenignCount = entry.getValue();  // Set the global benign count
            } else {
                totalAttackCount += entry.getValue();  // Accumulate the attack count
            }
        }

        // Output the original table with all individual attack types and benign instances
        System.out.printf("%-20s %-35s %20s %25s %25s%n", "Attack Category", "Attack Name", "Number of Instances", "Proportion", "Benign in Same File");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");

        if (globalTrafficCount.containsKey("BENIGN")) {
            int benignCount = globalTrafficCount.get("BENIGN");
            double proportion = (double) benignCount / totalInstances * 100;
            System.out.printf("%-20s %-35s %20d %24.6f%%%n", "normal", "BENIGN", benignCount, proportion);
        }

        // Leave a blank line, then output other attack types
        System.out.println();

        // Output other attack types with proportion and corresponding benign count in the same file
        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            if (!entry.getKey().equals("BENIGN")) {
                String category = "attack";  // Set all attack categories to "attack"
                int count = entry.getValue();
                double proportion = (double) count / totalInstances * 100;
                System.out.printf("%-20s %-35s %20d %24.6f%%%n", category, entry.getKey(), count, proportion);
            }
        }

        // Output the binary classification (BENIGN and ATTACK)
        System.out.println("\n--- Binary Classification Table ---");
        System.out.printf("%-20s %20s %25s%n", "Category", "Number of Instances", "Proportion");
        System.out.println("--------------------------------------------------------------");

        // Output benign count
        double benignProportion = (double) totalBenignCount / totalInstances * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "BENIGN", totalBenignCount, benignProportion);

        // Output attack count
        double attackProportion = (double) totalAttackCount / totalInstances * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "ATTACK", totalAttackCount, attackProportion);

        // Output categorized attack counts with individual attack types
        System.out.println("\n--- Categorized Attack Table ---");
        System.out.printf("%-20s %20s%n", "Category", "Number of Instances");
        System.out.println("-------------------------------------");
        for (Map.Entry<String, Map<String, Integer>> categoryEntry : categoryCount.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Integer> attackTypes = categoryEntry.getValue();
            int totalCategoryCount = attackTypes.values().stream().mapToInt(Integer::intValue).sum();

            // Print category total
            System.out.printf("%-20s %20d%n", category, totalCategoryCount);

            // Print each attack type under this category
            for (Map.Entry<String, Integer> attackEntry : attackTypes.entrySet()) {
                System.out.printf("  %-35s %10d%n", attackEntry.getKey(), attackEntry.getValue());
            }
        }
    }

    // Process individual CSV file and categorize attacks
    public static void processCSVFile(File csvFile, Map<String, Integer> globalTrafficCount, Map<String, Map<String, Integer>> categoryCount) {
        String line;
        String csvSplitBy = ",";
        int benignCount = 0;
        int totalRows = 0;
        int skippedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read file header
            String header = br.readLine();
            totalRows++;  // Count the header row
            System.out.println("Processing file: " + csvFile.getName() + ", Header: " + header);

            // Read data rows
            while ((line = br.readLine()) != null) {
                totalRows++;

                // Skip empty or improperly formatted lines
                if (line.trim().isEmpty()) {
                    skippedRows++;
                    continue;
                }

                String[] dataRow = line.split(csvSplitBy);

                // Assume the last column is the label
                int labelIndex = dataRow.length - 1;
                if (labelIndex < 0) {
                    skippedRows++;
                    continue;
                }

                String label = dataRow[labelIndex].trim();

                // Count occurrences of each label
                globalTrafficCount.put(label, globalTrafficCount.getOrDefault(label, 0) + 1);

                // If the label is an attack, categorize it
                if (!label.equalsIgnoreCase("BENIGN")) {
                    String category = attackCategoryMap.getOrDefault(label, "Unknown");

                    // Update categoryCount with individual attack types
                    categoryCount.putIfAbsent(category, new HashMap<>());
                    Map<String, Integer> attacksInCategory = categoryCount.get(category);
                    attacksInCategory.put(label, attacksInCategory.getOrDefault(label, 0) + 1);
                }

                // If the label is "BENIGN", increment benign count
                if (label.equalsIgnoreCase("BENIGN")) {
                    benignCount++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File " + csvFile.getName() + " Total rows: " + totalRows + ", Skipped rows: " + skippedRows);
    }
}
