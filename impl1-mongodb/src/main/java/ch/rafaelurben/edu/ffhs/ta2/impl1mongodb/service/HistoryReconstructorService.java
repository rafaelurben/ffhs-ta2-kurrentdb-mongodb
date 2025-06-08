/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObject;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObjectHistory;

public interface HistoryReconstructorService {
  ParentObject reconstructParentAtHistoryEntry(ParentObjectHistory history, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ParentObject restoreParentToHistoryEntry(ParentObjectHistory history, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException;
}
