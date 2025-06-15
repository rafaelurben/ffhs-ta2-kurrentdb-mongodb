/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import java.util.List;

public interface ObjectService {
  // Object management

  List<ParentObjectDto> getAllParentObjects();

  ParentObjectDto createParentObject(ParentObjectCreateDto parentObjectCreateDto);

  ParentObjectDto getParentObjectById(String parentId) throws ResourceNotFoundException;

  ParentObjectDto updateParentObject(String parentId, ParentObjectUpdateDto parentObjectUpdateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  void deleteParentObject(String parentId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ChildObjectDto createChildObject(String parentId, ChildObjectCreateDto childObjectCreateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ChildObjectDto getChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException;

  ChildObjectDto updateChildObjectById(
      String parentId, String childId, ChildObjectUpdateDto childObjectUpdateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  void deleteChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  // Object history

  List<HistoryEntryDto> getAllHistoryEntriesByParentId(String parentId)
      throws ResourceNotFoundException;

  ParentObjectDto previewParentAtHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ParentObjectDto restoreParentToHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException;
}
