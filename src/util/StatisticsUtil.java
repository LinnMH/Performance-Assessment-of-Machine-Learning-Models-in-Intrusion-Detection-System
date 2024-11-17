package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class StatisticsUtil {

//    public static void main(String[] args) throws IOException {
//        String csvFilePath = "D:\\csv\\IDS2018_test_replaced.csv"; // Path to your CSV file
//        String outputFilePath = "D:\\csv\\IDS2018_stats.csv";
//
//        try (Stream<String> lines = Files.lines(Path.of(csvFilePath))) {
//            // Read the first line to determine the number of columns
//            Iterator<String> iterator = lines.iterator();
//            if (!iterator.hasNext()) {
//                System.out.println("Empty file!");
//                return;
//            }
//            String header = iterator.next();
//            int columnCount = header.split(",").length;
//
//            // Initialize statistics containers for each column
//            List<DoubleSummaryStatistics> columnStats = new ArrayList<>();
//            List<RunningStats> runningStats = new ArrayList<>();
//            List<MedianFinder> medianFinders = new ArrayList<>();
//            for (int i = 0; i < columnCount; i++) {
//                columnStats.add(new DoubleSummaryStatistics());
//                runningStats.add(new RunningStats());
//                medianFinders.add(new MedianFinder());
//            }
//
//            try (Stream<String> dataLines = Files.lines(Path.of(csvFilePath)).skip(1)) {
//                dataLines.forEach(line -> {
//                    String[] values = line.split(",");
//                    for (int i = 0; i < columnCount; i++) {
//                        try {
//                            double value = Double.parseDouble(values[i].trim());
//                            columnStats.get(i).accept(value);
//                            runningStats.get(i).add(value);
//                            medianFinders.get(i).add(value);
//                        } catch (Exception ignored) {
//                            // Skip invalid data
//                        }
//                    }
//                });
//            }
//
//            String[] fields = header.split(",");
//            // Print statistics for each column
//            for (int i = 0; i < columnCount - 1; i++) {
//                System.out.printf("Column %d: Header: %s, Count: %d, Avg: %.2f, Min: %.2f, Max: %.2f, Median: %.2f, StdDev: %.2f%n",
//                        i + 1, fields[i], columnStats.get(i).getCount(), columnStats.get(i).getAverage(),
//                        columnStats.get(i).getMin(), columnStats.get(i).getMax(),
//                        medianFinders.get(i).getMedian(), runningStats.get(i).getStandardDeviation());
//            }
//
//            // Write results line by line
//            try (BufferedWriter writer = Files.newBufferedWriter(
//                    Path.of(outputFilePath))) {
//
//                // Write header
//                writer.write("Feature,Average,Min,Max,Median,StdDev");
//                writer.newLine();
//
//                // Write statistics for each column
//                for (int i = 0; i < columnCount - 1; i++) {
//                    String line = String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f",
//                            fields[i].trim(), columnStats.get(i).getAverage(),
//                            columnStats.get(i).getMin(), columnStats.get(i).getMax(),
//                            medianFinders.get(i).getMedian(), runningStats.get(i).getStandardDeviation());
//                    writer.write(line);
//                    writer.newLine();
//                }
//            }
//
//            System.out.println("Statistics written to " + outputFilePath);
//        }
//    }
//}

    public static void main(String[] args) throws IOException {
        String csvFilePath = "D:\\csv\\IDS2018_test_replaced.csv"; // Input CSV file path
        String outputFilePath = "D:\\csv\\IDS2018_stats.csv"; // Output CSV file path

        // Map to store statistics for each type
        Map<String, List<DoubleSummaryStatistics>> typeStats = new HashMap<>();
        Map<String, List<RunningStats>> typeRunningStats = new HashMap<>();
        Map<String, List<MedianFinder>> typeMedianFinders = new HashMap<>();
        Map<String, Integer> typeCounts = new HashMap<>();
        String[] columns;

        // Open the input file and process each line
        try (Stream<String> lines = Files.lines(Path.of(csvFilePath))) {
            Iterator<String> iterator = lines.iterator();
            if (!iterator.hasNext()) {
                System.out.println("Empty file!");
                return;
            }

            String header = iterator.next(); // Read header to determine columns
            columns = header.split(",");
            int columnCount = columns.length;

            // Initialize the statistics containers for each type and column
            while (iterator.hasNext()) {
                String line = iterator.next();
                String[] values = line.split(",");
                String type = values[columnCount - 1]; // Last column is the type

                // Initialize statistics for this type if not already done
                if (!typeStats.containsKey(type)) {
                    List<DoubleSummaryStatistics> columnStats = new ArrayList<>();
                    List<RunningStats> runningStats = new ArrayList<>();
                    List<MedianFinder> medianFinders = new ArrayList<>();
                    for (int i = 0; i < columnCount - 1; i++) { // Exclude the "type" column
                        columnStats.add(new DoubleSummaryStatistics());
                        runningStats.add(new RunningStats());
                        medianFinders.add(new MedianFinder());
                    }
                    typeStats.put(type, columnStats);
                    typeRunningStats.put(type, runningStats);
                    typeMedianFinders.put(type, medianFinders);
                    typeCounts.put(type, 0);
                }

                // Process each column, except the "type" column
                for (int i = 0; i < columnCount - 1; i++) {
                    try {
                        double value = Double.parseDouble(values[i].trim());
                        typeStats.get(type).get(i).accept(value);
                        typeRunningStats.get(type).get(i).add(value);
                        typeMedianFinders.get(type).get(i).add(value);
                    } catch (Exception ignored) {
                        // Ignore invalid values
                    }
                }

                // Increment the count for this type
                typeCounts.put(type, typeCounts.get(type) + 1);
            }
        }

        // Write the statistics to the output file
        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(outputFilePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            // Write the header for the output file
            writer.write("Label,Feature,Average,Min,Max,Median,StdDev");
            writer.newLine();

            // Write statistics for each type
            for (String type : typeStats.keySet()) {
                List<DoubleSummaryStatistics> columnStats = typeStats.get(type);
                List<RunningStats> runningStats = typeRunningStats.get(type);
                List<MedianFinder> medianFinders = typeMedianFinders.get(type);

                // Write statistics for each column under this type
                for (int i = 0; i < columnStats.size(); i++) {
                    String line = String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f",
                            type, columns[i].trim(),
                            columnStats.get(i).getAverage(), columnStats.get(i).getMin(),
                            columnStats.get(i).getMax(), medianFinders.get(i).getMedian(),
                            runningStats.get(i).getStandardDeviation());
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        System.out.println("Statistics written to " + outputFilePath);
    }
}
    class RunningStats {
        private long count = 0;
        private double mean = 0, sumSquaredDifferences = 0;

        public void add(double value) {
            count++;
            double delta = value - mean;
            mean += delta / count;
            sumSquaredDifferences += delta * (value - mean);
        }

        public double getStandardDeviation() {
            return count > 1 ? Math.sqrt(sumSquaredDifferences / (count - 1)) : 0.0;
        }
    }


    class MedianFinder {
        private final PriorityQueue<Double> lower = new PriorityQueue<>((a, b) -> Double.compare(b, a));
        private final PriorityQueue<Double> upper = new PriorityQueue<>();

        public void add(double value) {
            if (lower.isEmpty() || value <= lower.peek()) lower.add(value);
            else upper.add(value);
            if (lower.size() > upper.size() + 1) upper.add(lower.poll());
            if (upper.size() > lower.size()) lower.add(upper.poll());
        }

        public double getMedian() {
            return lower.size() == upper.size() ? (lower.peek() + upper.peek()) / 2.0 : lower.peek();
        }
    }
