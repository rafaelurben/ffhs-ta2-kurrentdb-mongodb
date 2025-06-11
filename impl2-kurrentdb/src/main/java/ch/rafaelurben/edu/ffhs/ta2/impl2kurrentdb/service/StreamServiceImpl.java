/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.config.KurrentDBConfig;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.EventBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kurrent.dbclient.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {
  private final KurrentDBConfig kurrentDBConfig;
  private final KurrentDBClient eventStore;
  private final ObjectMapper jsonMapper;

  public List<EventBase> readObjectStream(String parentId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ReadResult readResult;
    try {
      readResult =
          eventStore
              .readStream(
                  kurrentDBConfig.getStreamName(parentId), ReadStreamOptions.get().fromStart())
              .get();
    } catch (ExecutionException | InterruptedException e) {
      if (e.getCause() instanceof StreamNotFoundException) {
        throw new ResourceNotFoundException("Parent object with ID " + parentId + " not found.");
      }
      throw new RuntimeException(e);
    }

    List<EventBase> events =
        readResult.getEvents().stream()
            .map(ResolvedEvent::getOriginalEvent)
            .map(RecordedEvent::getEventData)
            .map(
                (eventData) -> {
                  try {
                    return jsonMapper.readValue(eventData, EventBase.class);
                  } catch (IOException e) {
                    throw new RuntimeException("Failed to parse event data", e);
                  }
                })
            .toList();

    if (events.isEmpty()) {
      throw new ResourceNotFoundException("No events found for parent object with ID " + parentId);
    }

    return events;
  }

  public void storeEvent(String parentId, EventBase event) {
    try {
      UUID eventId = UUID.randomUUID();
      event.setId(eventId.toString());
      byte[] eventBytes = jsonMapper.writeValueAsBytes(event);
      EventData eventData =
          EventData.builderAsJson(eventId, event.getChangeType().toString(), eventBytes).build();
      eventStore.appendToStream(kurrentDBConfig.getStreamName(parentId), eventData).get();
    } catch (ExecutionException | InterruptedException | JsonProcessingException e) {
      throw new RuntimeException("Failed to store event for parent ID " + parentId, e);
    }
  }
}
