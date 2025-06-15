/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.config.KurrentDBConfig;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.KurrentException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {
  private final KurrentDBConfig kurrentDBConfig;
  private final KurrentDBClient eventStore;
  private final ObjectMapper jsonMapper;

  private static final ThreadLocal<Long> lastRevision = ThreadLocal.withInitial(() -> -1L);

  private EventBase parseEventData(byte[] eventData) {
    try {
      return jsonMapper.readValue(eventData, EventBase.class);
    } catch (IOException e) {
      throw new KurrentException("Failed to parse event data", e);
    }
  }

  @Override
  public List<EventBase> readObjectStream(String parentId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ReadResult readResult;
    try {
      readResult =
          eventStore
              .readStream(
                  kurrentDBConfig.getStreamName(parentId), ReadStreamOptions.get().fromStart())
              .get();
      lastRevision.set(readResult.getLastStreamPosition());
      log.debug(
          "Got last revision for stream {}: {}", parentId, readResult.getLastStreamPosition());
    } catch (ExecutionException e) {
      if (e.getCause() instanceof StreamNotFoundException) {
        throw new ResourceNotFoundException("Parent object with ID " + parentId + " not found.");
      }
      throw new KurrentException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new KurrentException("Interrupted while reading stream for parent ID " + parentId, e);
    }

    List<EventBase> events =
        readResult.getEvents().stream()
            .map(ResolvedEvent::getOriginalEvent)
            .map(RecordedEvent::getEventData)
            .map(this::parseEventData)
            .toList();

    if (events.isEmpty()) {
      throw new ResourceNotFoundException("No events found for parent object with ID " + parentId);
    }

    return events;
  }

  private AppendToStreamOptions getDefaultAppendToStreamOptions() {
    if (lastRevision.get() == -1L) {
      return AppendToStreamOptions.get().streamState(StreamState.any());
    }
    return AppendToStreamOptions.get().streamRevision(lastRevision.get());
  }

  private EventData createEventData(EventBase event) {
    UUID eventId = UUID.randomUUID();
    event.setId(eventId.toString());
    try {
      byte[] eventBytes = jsonMapper.writeValueAsBytes(event);
      return EventData.builderAsJson(eventId, event.getChangeType().toString(), eventBytes).build();
    } catch (JsonProcessingException e) {
      throw new KurrentException("Failed to serialize event", e);
    }
  }

  @Override
  public void appendEvents(
      String parentId, List<EventBase> events, AppendToStreamOptions appendOptions) {
    try {
      EventData[] eventDataArray =
          events.stream().map(this::createEventData).toArray(EventData[]::new);

      log.debug(
          "Trying to append {} new event(s) to stream {} with options {}.",
          eventDataArray.length,
          parentId,
          appendOptions);

      eventStore
          .appendToStream(kurrentDBConfig.getStreamName(parentId), appendOptions, eventDataArray)
          .get();
    } catch (ExecutionException e) {
      throw new KurrentException("Failed to store event for parent ID " + parentId, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new KurrentException("Interrupted while storing event for parent ID " + parentId, e);
    }
  }

  @Override
  public void appendEvents(String parentId, List<EventBase> events) {
    appendEvents(parentId, events, getDefaultAppendToStreamOptions());
  }

  @Override
  public void appendEvent(String parentId, EventBase event, AppendToStreamOptions appendOptions) {
    appendEvents(parentId, List.of(event), appendOptions);
  }

  @Override
  public void appendEvent(String parentId, EventBase event) {
    appendEvents(parentId, List.of(event));
  }
}
