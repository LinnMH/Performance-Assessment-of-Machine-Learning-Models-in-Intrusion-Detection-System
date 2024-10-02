import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Directory2018CSVProcessor {

    // Mapping of attack types to categories (DoS, DDoS, Brute Force, etc.)
    private static final Map<String, String> attackCategoryMap = new HashMap<>();

    static {
        // Define mapping of each attack to its corresponding major category
        attackCategoryMap.put("DoS attacks-SlowLoris", "DoS (Denial of Service) Attacks");
        attackCategoryMap.put("DoS attacks-GoldenEye", "DoS (Denial of Service) Attacks");
        attackCategoryMap.put("DoS attacks-SlowHTTPTest", "DoS (Denial of Service) Attacks");
        attackCategoryMap.put("DoS attacks-Hulk", "DoS (Denial of Service) Attacks");

        attackCategoryMap.put("DDoS attack-HOIC", "DDoS (Distributed Denial of Service) Attacks");
        attackCategoryMap.put("DDoS attack-LOIC-UDP", "DDoS (Distributed Denial of Service) Attacks");
        attackCategoryMap.put("DDoS attacks-LOIC-HTTP", "DDoS (Distributed Denial of Service) Attacks");

        attackCategoryMap.put("FTP-BruteForce", "Brute Force Attacks");
        attackCategoryMap.put("SSH-Bruteforce", "Brute Force Attacks");
        attackCategoryMap.put("Brute Force -XSS", "Brute Force Attacks");
        attackCategoryMap.put("Brute Force -Web", "Brute Force Attacks");

        attackCategoryMap.put("SQL Injection", "Other Attacks");
        attackCategoryMap.put("Infiltration", "Other Attacks");

        attackCategoryMap.put("Bot", "Botnet");

        attackCategoryMap.put("Unknown", "Unknown (Uncategorized or General)");
    }

    public static void main(String[] args) {
        String directoryPath = "D:\\bowen\\CS-870\\week3\\CSE-CIC-IDS2018\\archive";  // Specify the directory path containing CSV files
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

        // Calculate total number of instances and separate benign and attack counts
        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            totalInstances += entry.getValue();
            if (entry.getKey().equalsIgnoreCase("BENIGN")) {
                totalBenignCount = entry.getValue();  // Set the global benign count
            } else {
                totalAttackCount += entry.getValue();  // Accumulate the attack count
            }
        }

        // --- First Table (Multiclass Table) ---
        System.out.println("\n--- Multiclass Table (All Attack Types) ---");
        System.out.printf("%-10s %-40s %20s%n", "Type", "Attack Type", "Number of Instances");
        System.out.println("------------------------------------------------------------");

        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            String attackType = entry.getKey();
            if (!attackType.equalsIgnoreCase("Label")) {  // Filter out the "Label"
                int count = entry.getValue();
                String type = attackType.equalsIgnoreCase("BENIGN") ? "normal" : "attack";
                System.out.printf("%-10s %-40s %20d%n", type, attackType, count);
            }
        }

        // --- Second Table (Binary Classification) ---
        System.out.println("\n--- Binary Classification Table (Benign vs. Attack) ---");
        System.out.printf("%-20s %20s %25s%n", "Category", "Number of Instances", "Proportion");
        System.out.println("--------------------------------------------------------------");

        // Output benign count
        double benignProportion = (double) totalBenignCount / totalInstances * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "BENIGN", totalBenignCount, benignProportion);

        // Output attack count
        double attackProportion = (double) totalAttackCount / totalInstances * 100;
        System.out.printf("%-20s %20d %24.6f%%%n", "ATTACK", totalAttackCount, attackProportion);

        // --- Third Table (Major Attack Categories and Sub-Attacks) ---
        System.out.println("\n--- Major Attack Categories and Sub-attacks ---");
        System.out.printf("%-40s %-40s %20s%n", "Major Attack", "Sub-Attack", "Number of Instances");
        System.out.println("--------------------------------------------------------------");

        // Loop through the categoryCount and output the major attacks and their sub-attacks
        for (Map.Entry<String, Map<String, Integer>> categoryEntry : categoryCount.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Integer> attackTypes = categoryEntry.getValue();

            // Print the major attack category
            System.out.printf("%-40s%n", category);

            // Print the sub-attacks under this major category
            for (Map.Entry<String, Integer> attackEntry : attackTypes.entrySet()) {
                System.out.printf("  %-35s %20d%n", attackEntry.getKey(), attackEntry.getValue());
            }
            System.out.println();
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
                if (!label.equalsIgnoreCase("BENIGN") && !label.equalsIgnoreCase("Label")) {  // Skip "Label"
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
