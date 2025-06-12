/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.performance;

import static ch.rafaelurben.edu.ffhs.ta2.testing.utils.TestingUtils.sleep;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.testing.utils.Metric;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManyChildrenTest {
  private static final int CHILD_COUNT = 1000;

  private ManyChildrenTest() {
    // Utility class
  }

  public static Metric[] run(ApiClient apiClient) throws ApiException, InterruptedException {
    long[] writeDurations = new long[CHILD_COUNT];
    long[] readParentDurations = new long[CHILD_COUNT];
    long[] readHistoryDurations = new long[CHILD_COUNT];
    long[] readBehindCounts = new long[CHILD_COUNT];

    log.info("ManyChildren Performance Test started.");

    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    ObjectHistoryApi objectHistoryApi = new ObjectHistoryApi(apiClient);
    var parent = objectsApi.createParent(new ParentObjectCreateDto().name("Many Children Test"));
    var parentId = parent.getId();

    sleep();

    for (int i = 0; i < CHILD_COUNT; i++) {
      if (i % 100 == 0) {
        log.info("Creating child objects... Progress: {}/{}", i, CHILD_COUNT);
      }

      long startTime = System.nanoTime();
      objectsApi.createChild(parentId, new ChildObjectCreateDto().name("Child " + i).value(i));
      long endTime = System.nanoTime();
      writeDurations[i] = endTime - startTime;

      startTime = System.nanoTime();
      var fetchedParent = objectsApi.getParent(parentId);
      endTime = System.nanoTime();
      readParentDurations[i] = endTime - startTime;
      readBehindCounts[i] = (long) i + 1 - fetchedParent.getChildren().size();

      startTime = System.nanoTime();
      objectHistoryApi.getEntriesByParentId(parentId);
      endTime = System.nanoTime();
      readHistoryDurations[i] = endTime - startTime;
    }

    objectsApi.deleteParent(parentId);

    log.info("ManyChildren Performance Test finished.");
    return new Metric[] {
      new Metric("Write Durations (ns)", writeDurations),
      new Metric("Read Parent Durations (ns)", readParentDurations),
      new Metric("Read History Durations (ns)", readHistoryDurations),
      new Metric("Read Behind Lag Count (events)", readBehindCounts)
    };
  }
}
