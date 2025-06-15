/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.EventBase;
import io.kurrent.dbclient.AppendToStreamOptions;
import io.kurrent.dbclient.StreamState;
import java.util.List;

public interface StreamService {
  List<EventBase> readObjectStream(String parentId) throws ResourceNotFoundException;

  void appendEvents(String parentId, List<EventBase> events, AppendToStreamOptions appendOptions);

  void appendEvents(String parentId, List<EventBase> events);

  void appendEvent(String parentId, EventBase event, AppendToStreamOptions appendOptions);

  void appendEvent(String parentId, EventBase event);

  static AppendToStreamOptions expectNoStream() {
    return AppendToStreamOptions.get().streamState(StreamState.noStream());
  }
}
