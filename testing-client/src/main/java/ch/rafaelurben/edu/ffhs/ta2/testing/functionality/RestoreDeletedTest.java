/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.functionality;

import static ch.rafaelurben.edu.ffhs.ta2.testing.utils.TestingUtils.*;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectCreateDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestoreDeletedTest {
  private RestoreDeletedTest() {
    // Utility class
  }

  private static final String PARENT_NAME = "Parent-Restore";
  private static final String CHILD_NAME = "Child-Restore";

  public static void test(ApiClient apiClient) throws ApiException, InterruptedException {
    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    ObjectHistoryApi objectHistoryApi = new ObjectHistoryApi(apiClient);

    try {
      // Create a new parent object
      ParentObjectCreateDto parentCreate = new ParentObjectCreateDto();
      parentCreate.setName(PARENT_NAME);
      var parent = objectsApi.createParent(parentCreate);

      // Create a new child object
      ChildObjectCreateDto childCreate = new ChildObjectCreateDto();
      childCreate.setName(CHILD_NAME);
      childCreate.setValue(42);
      objectsApi.createChild(parent.getId(), childCreate);

      // sleep
      sleep();

      // Fetch the parent object and check if it is there and has 1 child
      var fetchedParent = objectsApi.getParent(parent.getId());
      assertEquals(PARENT_NAME, fetchedParent.getName(), "Parent name not correct");
      assertEquals(1, fetchedParent.getChildren().size(), "Parent should have 1 child");
      assertEquals(
          CHILD_NAME,
          fetchedParent.getChildren().getFirst().getName(),
          "Fetched child name not correct");

      // Delete the parent object
      objectsApi.deleteParent(parent.getId());

      // sleep
      sleep();

      // Fetch the parent object and expect 404
      assert404(
          () -> objectsApi.getParent(parent.getId()), "Expected 404 when fetching deleted parent");

      // Restore the parent object to the second history entry
      var historyList = objectHistoryApi.getEntriesByParentId(parent.getId());
      if (historyList.size() != 3) {
        throw new AssertionError(
            "History list should have 3 entries but has: " + historyList.size());
      }
      var secondHistoryId = historyList.get(1).getChildCreatedEventDto().getId();
      var restoredParent =
          objectHistoryApi.restoreParentToHistoryEntry(parent.getId(), secondHistoryId);
      assertEquals(PARENT_NAME, restoredParent.getName(), "Restored parent name not correct");
      assertEquals(1, restoredParent.getChildren().size(), "Restored parent should have 1 child");
      assertEquals(
          CHILD_NAME,
          restoredParent.getChildren().getFirst().getName(),
          "Restored child name not correct");

      // sleep
      sleep();

      // Fetch the parent object and check if it is there and has 1 child
      var fetchedParentAgain = objectsApi.getParent(parent.getId());
      assertEquals(
          PARENT_NAME, fetchedParentAgain.getName(), "Parent name not correct after restore");
      assertEquals(
          1, fetchedParentAgain.getChildren().size(), "Parent should have 1 child after restore");
      assertEquals(
          CHILD_NAME,
          restoredParent.getChildren().getFirst().getName(),
          "Restored child name not correct");

      // Delete the parent object
      objectsApi.deleteParent(parent.getId());

      log.info("RestoreDeletedTest passed");
    } catch (ApiException e) {
      log.error("API Exception:", e);
      throw e;
    }
  }
}
