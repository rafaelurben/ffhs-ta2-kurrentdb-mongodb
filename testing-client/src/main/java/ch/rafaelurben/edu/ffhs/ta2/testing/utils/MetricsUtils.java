/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsUtils {
  private MetricsUtils() {
    // Utility class
  }

  public static void exportMetricsToCsv(String title, Metric[] metrics) {
    int rows = metrics[0].dataPoints().length;
    StringBuilder csvBuilder = new StringBuilder();
    String headerRow = Arrays.stream(metrics).map(Metric::label).collect(Collectors.joining(","));
    csvBuilder.append(headerRow).append("\n");
    for (int i = 0; i < rows; i++) {
      // Use final variable to avoid lambda capture issues
      int finalI = i;
      String row =
          Arrays.stream(metrics)
              .map(metric -> String.valueOf(metric.dataPoints()[finalI]))
              .collect(Collectors.joining(","));
      csvBuilder.append(row).append("\n");
    }

    String csvContent = csvBuilder.toString();
    Path folderPath = Path.of("stats", "data");
    Path filePath = folderPath.resolve(title + ".csv");
    try {
      Files.createDirectories(folderPath);
      Files.writeString(
          filePath, csvContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      log.info("Metrics exported to 'stats/data/{}'", filePath);
    } catch (IOException e) {
      log.error("Failed to write metrics to CSV file:", e);
    }
  }
}
