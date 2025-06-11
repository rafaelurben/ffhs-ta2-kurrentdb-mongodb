/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.EventBase;
import java.util.List;

public interface StreamService {
  List<EventBase> readObjectStream(String parentId) throws ResourceNotFoundException;

  void storeEvent(String parentId, EventBase event);
}
