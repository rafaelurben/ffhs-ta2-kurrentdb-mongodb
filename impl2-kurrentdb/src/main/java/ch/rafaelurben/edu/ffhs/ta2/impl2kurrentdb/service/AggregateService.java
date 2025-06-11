/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.EventBase;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import java.util.List;

public interface AggregateService {
  ParentObjectDto constructParentFromEvents(List<EventBase> events)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ParentObjectDto constructParentFromEventsUpToHistoryId(
      List<EventBase> events, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException;

  ParentObjectDto restoreParentToHistoryEntry(
      List<EventBase> events, String targetHistoryId, List<EventBase> newEventsToApply)
      throws ResourceNotFoundException, ImpossibleHistoryException;
}
