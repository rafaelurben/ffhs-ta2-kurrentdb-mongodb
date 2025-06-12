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
public class Example2Test {
  private Example2Test() {
    // Utility class
  }

  private static final String PARENT1 = "Parent1";
  private static final String PARENT1_UPDATED = "Parent1-updated";
  private static final String CHILD1 = "Child1";
  private static final String CHILD2 = "Child2";
  private static final String CHILD2_UPDATED = "Child2-updated";
  private static final String CHILD3 = "Child3";

  public static void test(ApiClient apiClient) throws ApiException, InterruptedException {
    ObjectsApi objectsApi = new ObjectsApi(apiClient);
    ObjectHistoryApi objectHistoryApi = new ObjectHistoryApi(apiClient);

    try {
      // create a new parent object
      ParentObjectCreateDto parentCreate = new ParentObjectCreateDto();
      parentCreate.setName(PARENT1);
      var parent = objectsApi.createParent(parentCreate);

      // create 3 child objects
      ChildObjectCreateDto childCreate1 = new ChildObjectCreateDto();
      childCreate1.setName(CHILD1);
      childCreate1.setValue(15);
      var child1 = objectsApi.createChild(parent.getId(), childCreate1);

      ChildObjectCreateDto childCreate2 = new ChildObjectCreateDto();
      childCreate2.setName(CHILD2);
      childCreate2.setValue(30);
      var child2 = objectsApi.createChild(parent.getId(), childCreate2);

      ChildObjectCreateDto childCreate3 = new ChildObjectCreateDto();
      childCreate3.setName(CHILD3);
      childCreate3.setValue(45);
      objectsApi.createChild(parent.getId(), childCreate3);

      // update the second child object
      ChildObjectUpdateDto childUpdate2 = new ChildObjectUpdateDto();
      childUpdate2.setName(CHILD2_UPDATED);
      childUpdate2.setValueChange(-5);
      var updatedChild2 = objectsApi.updateChild(parent.getId(), child2.getId(), childUpdate2);
      assertEquals(CHILD2_UPDATED, updatedChild2.getName(), "Child2 name not updated correctly");
      assertEquals(25, updatedChild2.getValue(), "Child2 value not updated correctly");

      // delete the first child object
      objectsApi.deleteChild(parent.getId(), child1.getId());

      // update the parent object
      var parentUpdate = new ParentObjectUpdateDto();
      parentUpdate.setName(PARENT1_UPDATED);
      objectsApi.updateParent(parent.getId(), parentUpdate);

      // wait for write operations to be processed
      sleep();

      // get the parent object
      var fetchedParent = objectsApi.getParent(parent.getId());
      assertEquals(PARENT1_UPDATED, fetchedParent.getName(), "Parent name not updated");

      // get the second child object and check again if it is really updated
      var fetchedChild2 = objectsApi.getChild(parent.getId(), child2.getId());
      assertEquals(CHILD2_UPDATED, fetchedChild2.getName(), "Child2 name not updated correctly");
      assertEquals(25, fetchedChild2.getValue(), "Child2 value not updated correctly");

      // get the history of the parent object
      var historyList = objectHistoryApi.getEntriesByParentId(parent.getId());
      if (historyList == null || historyList.size() < 3) {
        throw new AssertionError("Not enough history entries");
      }

      // preview the parent object at the third history entry and check if only the first two
      // children are present in their original form
      var thirdHistoryId = historyList.get(2).getChildCreatedEventDto().getId();
      var previewParent =
          objectHistoryApi.previewParentAtHistoryEntry(parent.getId(), thirdHistoryId);
      assertEquals(
          2, previewParent.getChildren().size(), "Previewed parent should have 2 children");
      boolean hasChild1 =
          previewParent.getChildren().stream().anyMatch(c -> c.getName().equals(CHILD1));
      boolean hasChild2 =
          previewParent.getChildren().stream().anyMatch(c -> c.getName().equals(CHILD2));
      if (!hasChild1 || !hasChild2) {
        throw new AssertionError("Previewed children are not correct");
      }

      // restore the parent object to the third history entry and check if only the first two
      // children are present in their original form
      var restoredParent =
          objectHistoryApi.restoreParentToHistoryEntry(parent.getId(), thirdHistoryId);
      assertEquals(
          2, restoredParent.getChildren().size(), "Restored parent should have 2 children");
      hasChild1 = restoredParent.getChildren().stream().anyMatch(c -> c.getName().equals(CHILD1));
      hasChild2 = restoredParent.getChildren().stream().anyMatch(c -> c.getName().equals(CHILD2));
      if (!hasChild1 || !hasChild2) {
        throw new AssertionError("Restored children are not correct");
      }

      // wait for the restore to be processed
      sleep();

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
