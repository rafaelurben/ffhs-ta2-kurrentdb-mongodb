/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.performance;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectDto;
import ch.rafaelurben.edu.ffhs.ta2.testing.utils.Metric;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManyParentsTest {
  private static final int PARENT_COUNT = 1000;

  private ManyParentsTest() {
    // Utility class
  }

  public static Metric[] run(ApiClient apiClient) throws ApiException, InterruptedException {
    long[] writeDurations = new long[PARENT_COUNT];
    long[] readAllDurations = new long[PARENT_COUNT];
    long[] readAllBehindCounts = new long[PARENT_COUNT];

    log.info("ManyParents Performance Test started.");

    String[] newParentIds = new String[PARENT_COUNT];
    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    List<ParentObjectDto> initialParents = objectsApi.getAllParents();
    int initialCount = initialParents.size();

    for (int i = 0; i < PARENT_COUNT; i++) {
      if (i % 100 == 0) {
        log.info("Creating parent objects... Progress: {}/{}", i, PARENT_COUNT);
      }

      long startTime = System.nanoTime();
      ParentObjectDto parent =
          objectsApi.createParent(new ParentObjectCreateDto().name("Parent " + i));
      long endTime = System.nanoTime();
      writeDurations[i] = endTime - startTime;
      newParentIds[i] = parent.getId();

      startTime = System.nanoTime();
      List<ParentObjectDto> allParents = objectsApi.getAllParents();
      endTime = System.nanoTime();
      readAllDurations[i] = endTime - startTime;
      int expectedCount = initialCount + i + 1;
      int actualCount = allParents.size();
      readAllBehindCounts[i] = (long) expectedCount - actualCount;
    }

    // Cleanup: delete all new parents
    log.info("Cleaning up created parent objects.");
    for (String id : newParentIds) {
      try {
        objectsApi.deleteParent(id);
      } catch (ApiException e) {
        log.warn("Failed to delete parent {}: {}", id, e.getMessage());
      }
    }

    log.info("ManyParents Performance Test finished.");
    return new Metric[] {
      new Metric("Write Durations (ns)", writeDurations),
      new Metric("Read All Durations (ns)", readAllDurations),
      new Metric("Read All Behind Lag Count (events)", readAllBehindCounts)
    };
  }
}
