/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.controller;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service.ObjectService;
import ch.rafaelurben.edu.ffhs.ta2.server.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.HistoryEntryDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ObjectHistoryController implements ObjectHistoryApi {
  ObjectService objectService;

  @Override
  public ResponseEntity<List<HistoryEntryDto>> getEntriesByParentId(String parentId) {
    try {
      List<HistoryEntryDto> historyEntries = objectService.getAllHistoryEntriesByParentId(parentId);
      return ResponseEntity.ok(historyEntries);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<ParentObjectDto> previewParentAtHistoryEntry(
      String parentId, String historyId) {
    try {
      ParentObjectDto parentObject = objectService.previewParentAtHistoryEntry(parentId, historyId);
      return ResponseEntity.ok(parentObject);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<ParentObjectDto> restoreParentToHistoryEntry(
      String parentId, String historyId) {
    try {
      ParentObjectDto restoredParent =
          objectService.restoreParentToHistoryEntry(parentId, historyId);
      return ResponseEntity.ok(restoredParent);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
