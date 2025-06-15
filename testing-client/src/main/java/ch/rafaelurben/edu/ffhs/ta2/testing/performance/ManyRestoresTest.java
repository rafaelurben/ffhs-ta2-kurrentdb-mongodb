/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.performance;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.testing.utils.Metric;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManyRestoresTest {
  private static final int CHILD_COUNT = 10;
  private static final int RESTORE_COUNT = 8;

  private ManyRestoresTest() {
    // Utility class
  }

  public static Metric[] run(ApiClient apiClient) throws ApiException, InterruptedException {
    int oldReadTimeout = apiClient.getReadTimeout();
    apiClient.setReadTimeout(60000);

    long[] readHistoryDurations = new long[RESTORE_COUNT];
    long[] restoreDurations = new long[RESTORE_COUNT];
    long[] totalEventCounts = new long[RESTORE_COUNT];

    log.info("ManyRestores Performance Test started.");

    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    ObjectHistoryApi objectHistoryApi = new ObjectHistoryApi(apiClient);
    var parent = objectsApi.createParent(new ParentObjectCreateDto().name("Many Restores Test"));
    var parentId = parent.getId();

    log.info("Creating children...");
    for (int i = 0; i < CHILD_COUNT; i++) {
      objectsApi.createChild(parentId, new ChildObjectCreateDto().name("Child " + i).value(i));
    }
    log.info("Children created. Total children: {}", CHILD_COUNT);

    log.info("Restoring parent {} times...", RESTORE_COUNT);
    for (int i = 0; i < RESTORE_COUNT; i++) {
      log.info("Restoring parent... Progress: {}/{}", i, RESTORE_COUNT);

      long startTime = System.nanoTime();
      var historyList = objectHistoryApi.getEntriesByParentId(parentId);
      var firstHistoryId = historyList.getFirst().getParentCreatedEventDto().getId();
      long endTime = System.nanoTime();
      readHistoryDurations[i] = endTime - startTime;

      startTime = System.nanoTime();
      objectHistoryApi.restoreParentToHistoryEntry(parentId, firstHistoryId);
      endTime = System.nanoTime();
      restoreDurations[i] = endTime - startTime;

      totalEventCounts[i] = historyList.size();
    }

    log.info("Deleting parent...");
    objectsApi.deleteParent(parentId);

    apiClient.setReadTimeout(oldReadTimeout);
    log.info("ManyRestores Performance Test finished.");
    return new Metric[] {
      new Metric("Read History Durations (ns)", readHistoryDurations),
      new Metric("Restore Durations (ns)", restoreDurations),
      new Metric("Total Event Counts (events)", totalEventCounts)
    };
  }
}
