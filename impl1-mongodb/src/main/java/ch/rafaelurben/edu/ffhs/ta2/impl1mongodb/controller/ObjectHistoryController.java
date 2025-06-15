/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.controller;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service.ObjectService;
import ch.rafaelurben.edu.ffhs.ta2.server.api.ObjectHistoryApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.HistoryEntryDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ObjectHistoryController implements ObjectHistoryApi {
  private final ObjectService objectService;

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<List<HistoryEntryDto>> getEntriesByParentId(String parentId) {
    List<HistoryEntryDto> historyEntries = objectService.getAllHistoryEntriesByParentId(parentId);
    return ResponseEntity.ok(historyEntries);
  }

  @Override
  @SneakyThrows({ResourceNotFoundException.class, ImpossibleHistoryException.class})
  public ResponseEntity<ParentObjectDto> previewParentAtHistoryEntry(
      String parentId, String historyId) {
    ParentObjectDto parentObject = objectService.previewParentAtHistoryEntry(parentId, historyId);
    return ResponseEntity.ok(parentObject);
  }

  @Override
  @SneakyThrows({ResourceNotFoundException.class, ImpossibleHistoryException.class})
  public ResponseEntity<ParentObjectDto> restoreParentToHistoryEntry(
      String parentId, String historyId) {
    ParentObjectDto restoredParent = objectService.restoreParentToHistoryEntry(parentId, historyId);
    return ResponseEntity.ok(restoredParent);
  }
}
