/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.functionality;

import static ch.rafaelurben.edu.ffhs.ta2.testing.utils.TestingUtils.*;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.client.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ChildObjectUpdateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ParentObjectUpdateDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Example1Test {
  private Example1Test() {
    // Utility class
  }

  private static final String PARENT1 = "Parent1";
  private static final String PARENT1_UPDATED = "Parent1-updated";
  private static final String CHILD1 = "Child1";
  private static final String CHILD1_UPDATED = "Child1-updated";
  private static final String CHILD2 = "Child2";

  public static void test(ApiClient apiClient) throws ApiException, InterruptedException {
    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    ObjectHistoryApi objectHistoryApi = new ObjectHistoryApi(apiClient);

    try {
      // create a new parent object
      ParentObjectCreateDto parentCreate = new ParentObjectCreateDto();
      parentCreate.setName(PARENT1);
      var parent = objectsApi.createParent(parentCreate);

      // update the parent object
      ParentObjectUpdateDto parentUpdate = new ParentObjectUpdateDto();
      parentUpdate.setName(PARENT1_UPDATED);
      var updatedParent = objectsApi.updateParent(parent.getId(), parentUpdate);
      assertEquals(PARENT1_UPDATED, updatedParent.getName(), "Parent name not updated in response");

      // create a new child object
      ChildObjectCreateDto childCreate1 = new ChildObjectCreateDto();
      childCreate1.setName(CHILD1);
      childCreate1.setValue(10);
      var child1 = objectsApi.createChild(parent.getId(), childCreate1);

      // create a second child object
      ChildObjectCreateDto childCreate2 = new ChildObjectCreateDto();
      childCreate2.setName(CHILD2);
      childCreate2.setValue(20);
      var child2 = objectsApi.createChild(parent.getId(), childCreate2);

      // update the first child object
      ChildObjectUpdateDto childUpdate1 = new ChildObjectUpdateDto();
      childUpdate1.setName(CHILD1_UPDATED);
      childUpdate1.setValueChange(5);
      var updatedChild1 = objectsApi.updateChild(parent.getId(), child1.getId(), childUpdate1);
      assertEquals(CHILD1_UPDATED, updatedChild1.getName(), "Child1 name not updated");
      assertEquals(15, updatedChild1.getValue(), "Child1 value not updated");

      // delete the second child object
      objectsApi.deleteChild(parent.getId(), child2.getId());

      // wait for write operations to complete
      sleep();

      // get the parent object
      var fetchedParent = objectsApi.getParent(parent.getId());
      assertEquals(PARENT1_UPDATED, fetchedParent.getName(), "Parent name not updated in db");
      assertEquals(1, fetchedParent.getChildren().size(), "Parent should have 1 child");

      // get the first child object
      var fetchedChild1 = objectsApi.getChild(parent.getId(), child1.getId());
      assertEquals(CHILD1_UPDATED, fetchedChild1.getName(), "Child1 name not updated");
      assertEquals(15, fetchedChild1.getValue(), "Child1 value not updated");

      // get the second child object (expecting 404)
      assert404(
          () -> objectsApi.getChild(parent.getId(), child2.getId()),
          "Expected 404 when fetching deleted child2");

      // get the history of the parent object (expect 6 entries)
      var historyList = objectHistoryApi.getEntriesByParentId(parent.getId());
      assertEquals(6, historyList.size(), "Expected 6 history entries");
      var secondHistoryId = historyList.get(1).getParentUpdatedEventDto().getId();

      // preview the parent object at the second history entry (expect no children and updated name)
      var previewParent =
          objectHistoryApi.previewParentAtHistoryEntry(parent.getId(), secondHistoryId);
      assertEquals(PARENT1_UPDATED, previewParent.getName(), "Previewed parent name not updated");
      assertEquals(
          0, previewParent.getChildren().size(), "Previewed parent should have no children");

      // restore the parent object to the second history entry (expect no children and updated name)
      var restoredParent =
          objectHistoryApi.restoreParentToHistoryEntry(parent.getId(), secondHistoryId);
      assertEquals(PARENT1_UPDATED, restoredParent.getName(), "Parent name not updated");
      assertEquals(
          0, restoredParent.getChildren().size(), "Restored parent should have no children");

      // wait for the restore to be processed
      sleep();

      // get the parent object again (expect no children and updated name)
      var fetchedParentAgain = objectsApi.getParent(parent.getId());
      assertEquals(PARENT1_UPDATED, fetchedParentAgain.getName(), "Parent name not updated");
      assertEquals(0, fetchedParentAgain.getChildren().size(), "Parent should have no children");

      // delete the parent object
      objectsApi.deleteParent(parent.getId());

      // wait for the deletion to be processed
      sleep();

      // try to get the parent object again (expecting 404)
      assert404(
          () -> objectsApi.getParent(parent.getId()), "Expected 404 when fetching deleted parent");

      log.info("Tests passed");
    } catch (ApiException e) {
      log.error("API Exception:", e);
      throw e;
    }
  }
}
