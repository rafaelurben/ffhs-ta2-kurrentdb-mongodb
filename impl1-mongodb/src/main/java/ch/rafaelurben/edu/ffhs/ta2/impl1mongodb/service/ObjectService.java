/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import java.util.List;

public interface ObjectService {
  // Object management

  List<ParentObjectDto> getAllParentObjects();

  ParentObjectDto createParentObject(ParentObjectCreateDto parentObjectCreateDto);

  ParentObjectDto getParentObjectById(String parentId) throws ResourceNotFoundException;

  ParentObjectDto updateParentObject(String parentId, ParentObjectUpdateDto parentObjectUpdateDto)
      throws ResourceNotFoundException;

  void deleteParentObject(String parentId) throws ResourceNotFoundException;

  ChildObjectDto createChildObject(String parentId, ChildObjectCreateDto childObjectCreateDto)
      throws ResourceNotFoundException;

  ChildObjectDto getChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException;

  ChildObjectDto updateChildObjectById(
      String parentId, String childId, ChildObjectUpdateDto childObjectUpdateDto)
      throws ResourceNotFoundException;

  void deleteChildObjectById(String parentId, String childId) throws ResourceNotFoundException;

  // Object history

  List<HistoryEntryDto> getAllHistoryEntriesByParentId(String parentId)
      throws ResourceNotFoundException;

  ParentObjectDto previewParentAtHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ParentObjectDto restoreParentToHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException;
}
