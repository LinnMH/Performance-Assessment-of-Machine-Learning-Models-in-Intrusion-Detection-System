package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectoryCSVProcessor {
    public static void main(String[] args) {
        String directoryPath = "D:\\bowen\\CS-870\\week3\\MachineLearningCVE";  // Specify the directory path containing CSV files
        File folder = new File(directoryPath);
        Map<String, Integer> globalTrafficCount = new HashMap<>();  // Global statistics map for all files
        Map<String, Integer> benignCountPerFile = new HashMap<>();  // Map to store benign count per file
        int totalBenignCount = 0;  // Variable to store the global benign count
        int totalInstances = 0;  // Variable to store the total number of instances

        // Process all CSV files in the directory
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.getName().endsWith(".csv")) {
                processCSVFile(fileEntry, globalTrafficCount, benignCountPerFile);
            }
        }

        // Calculate the total number of instances and the global benign count
        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            totalInstances += entry.getValue();
            if (entry.getKey().equalsIgnoreCase("BENIGN")) {
                totalBenignCount = entry.getValue();  // Set the global benign count
            }
        }

        // Output with an added column for the proportion of each attack type
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
                int benignCount = benignCountPerFile.getOrDefault(entry.getKey(), 0);  // Get benign count for this attack type's file
                System.out.printf("%-20s %-35s %20d %24.6f%%%25d%n", category, entry.getKey(), count, proportion, benignCount);
            }
        }

        // Generate tables for each attack type showing Benign, attack count, and proportion relative to total instances
        generateAttackTables(globalTrafficCount, benignCountPerFile, totalBenignCount, totalInstances);
    }

    // Generate individual tables for each attack type
    public static void generateAttackTables(Map<String, Integer> globalTrafficCount, Map<String, Integer> benignCountPerFile, int totalBenignCount, int totalInstances) {
        System.out.println("\n--- Individual Attack Tables ---");

        boolean isFirstTable = true;

        for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
            if (!entry.getKey().equals("BENIGN")) {
                String attackType = entry.getKey();
                int attackCount = entry.getValue();
                int benignCount;

                // If the attack type contains "Web Attack", use the global benign count
                if (attackType.toLowerCase().contains("web attack")) {
                    benignCount = totalBenignCount;  // Use global benign count for Web Attack
                } else if (isFirstTable) {
                    benignCount = benignCountPerFile.getOrDefault(attackType, 0);  // First table uses per-file benign count
                } else {
                    benignCount = totalBenignCount;  // Subsequent tables use global benign count
                }

                // Calculate proportion of attack count relative to total instances (attack + benign)
                double proportionToTotal = (double) attackCount / totalInstances * 100;

                // Print table for each attack type
                System.out.println("\nAttack: " + attackType);
                System.out.printf("%-25s %-25s %-25s%n", "Benign Count", "Attack Count", "Proportion to Total");
                System.out.printf("%-25d %-25d %-25.6f%%%n", benignCount, attackCount, proportionToTotal);

                // After the first table, switch to using the global benign count for subsequent tables
                isFirstTable = false;
            }
        }
    }

    // Process individual CSV file
    public static void processCSVFile(File csvFile, Map<String, Integer> globalTrafficCount, Map<String, Integer> benignCountPerFile) {
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

                // If the label is "BENIGN", increment benign count
                if (label.equalsIgnoreCase("BENIGN")) {
                    benignCount++;
                }
            }

            // For each attack type in this file, associate the benign count with it
            for (Map.Entry<String, Integer> entry : globalTrafficCount.entrySet()) {
                if (!entry.getKey().equals("BENIGN")) {
                    benignCountPerFile.put(entry.getKey(), benignCount);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File " + csvFile.getName() + " Total rows: " + totalRows + ", Skipped rows: " + skippedRows);
    }
}
