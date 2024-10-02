import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectoryNB15CSVProcessor {

    public static void main(String[] args) {
        // Set the directory path where your CSV files are located
        String directoryPath = "D:\\bowen\\CS-870\\week3\\NB15\\CSV Files";  // Replace with your actual path
        File folder = new File(directoryPath);

        Map<String, Integer> attackCount = new HashMap<>();
        int[] benignCount = {0};  // Using an array to ensure mutability, allowing proper updates in methods
        int[] attackTotal = {0};  // Total count for binary classification attacks

        // Process each CSV file in the directory
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

        // Calculate total traffic
        int totalTraffic = benignCount[0] + attackTotal[0];

        // Output filtered attack type statistics, filtering out attack types with only 1 instance
        System.out.println("\n--- Filtered Attack Count ---");
        System.out.printf("%-20s %-35s %20s%n", "Category", "Type", "Number of Instances");
        System.out.println("-------------------------------------------------------------");

        for (Map.Entry<String, Integer> entry : attackCount.entrySet()) {
            if (entry.getValue() > 1) {  // Filter out attack types with only 1 instance
                System.out.printf("%-20s %-35s %20d%n", "attack", entry.getKey(), entry.getValue());
            }
        }

        // Output binary classification table
        System.out.println("\n--- Binary Classification Table ---");
        System.out.printf("%-20s %20s %25s%n", "Category", "Number of Instances", "Proportion");
        System.out.println("--------------------------------------------------------------");

        // Output BENIGN statistics
        double benignProportion = (double) benignCount[0] / totalTraffic * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "BENIGN", benignCount[0], benignProportion);

        // Output ATTACK statistics
        double attackProportion = (double) attackTotal[0] / totalTraffic * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "ATTACK", attackTotal[0], attackProportion);

        // Output categorized attack statistics table
        System.out.println("\n--- Categorized Attack Table ---");
        System.out.printf("%-20s %35s %20s%n", "Category", "Attack Type", "Number of Instances");
        System.out.println("---------------------------------------------------------------------");

        // Iterate through attack types, output categorized table, filtering out attacks with only 1 instance
        Map<String, Map<String, Integer>> categorizedAttackCount = categorizeAttacks(attackCount);

        for (Map.Entry<String, Map<String, Integer>> categoryEntry : categorizedAttackCount.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Integer> attacks = categoryEntry.getValue();
            int totalCategoryCount = attacks.values().stream().mapToInt(Integer::intValue).sum();

            // Output total number of attacks for the category
            if (totalCategoryCount > 1) {  // Filter out categories with only 1 instance
                System.out.printf("%-20s %35s %20d%n", category, category, totalCategoryCount);

                // Output each specific attack type in the category
                for (Map.Entry<String, Integer> attackEntry : attacks.entrySet()) {
                    if (attackEntry.getValue() > 1) {  // Filter out specific attacks with only 1 instance
                        System.out.printf("%-20s %35s %20d%n", "", attackEntry.getKey(), attackEntry.getValue());
                    }
                }
            }
        }
    }

    // Process a single CSV file and count each attack type and benign instances
    public static void processCSVFile(File csvFile, Map<String, Integer> attackCount, int[] benignCount, int[] attackTotal) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine();  // Skip the file header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;  // Skip empty lines
                }

                String[] dataRow = line.split(csvSplitBy);
                int labelIndex = dataRow.length - 1;  // Last column (indicating attack or benign)
                int attackTypeIndex = dataRow.length - 2;  // Second to last column (indicating attack type)

                if (labelIndex < 0 || attackTypeIndex < 0) {
                    continue;  // Skip rows with incorrect format
                }

                String label = dataRow[labelIndex].trim();  // Get the value of the last column
                String attackType = dataRow[attackTypeIndex].trim();  // Get the attack type from second to last column

                if (label.equals("1")) {  // If the last column is 1, it's an attack
                    attackCount.put(attackType, attackCount.getOrDefault(attackType, 0) + 1);
                    attackTotal[0]++;  // Increment total attack count
                } else if (label.equals("0")) {  // If the last column is 0, it's benign
                    benignCount[0]++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Categorize attacks by type
    public static Map<String, Map<String, Integer>> categorizeAttacks(Map<String, Integer> attackCount) {
        Map<String, Map<String, Integer>> categorized = new HashMap<>();

        for (Map.Entry<String, Integer> entry : attackCount.entrySet()) {
            String attackType = entry.getKey();
            int count = entry.getValue();

            // Simple classification based on attack type (can be further refined based on need)
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

            // Update category mapping
            categorized.putIfAbsent(category, new HashMap<>());
            categorized.get(category).put(attackType, count);
        }

        return categorized;
    }
}
