/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper.ChildObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper.ParentObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.*;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AggregateServiceImpl implements AggregateService {

  private final ParentObjectMapper parentObjectMapper;
  private final ChildObjectMapper childObjectMapper;

  private ParentObjectDto applyEvent(
      ParentObjectDto parentObjectDto, ParentCreatedEvent parentCreatedEvent)
      throws ImpossibleHistoryException {
    if (parentObjectDto != null) {
      throw new ImpossibleHistoryException(
          "Parent object is not null when trying to create parent");
    }
    return parentCreatedEvent.getCreateData();
  }

  private ParentObjectDto applyEvent(
      ParentObjectDto parentObjectDto, ParentUpdatedEvent parentUpdatedEvent)
      throws ImpossibleHistoryException {
    if (parentObjectDto == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to update parent");
    }
    parentObjectMapper.updateDto(parentObjectDto, parentUpdatedEvent.getChangeData());
    return parentObjectDto;
  }

  private ParentObjectDto applyEvent(ParentObjectDto parentObjectDto)
      throws ImpossibleHistoryException {
    if (parentObjectDto == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to delete parent");
    }
    return null;
  }

  private ParentObjectDto applyEvent(
      ParentObjectDto parentObjectDto, ChildCreatedEvent childCreatedEvent)
      throws ImpossibleHistoryException {
    if (parentObjectDto == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to create a child");
    }
    ChildObjectDto childObjectDto = childCreatedEvent.getCreateData();
    parentObjectDto.getChildren().add(childObjectDto);
    return parentObjectDto;
  }

  private ParentObjectDto applyEvent(
      ParentObjectDto parentObjectDto, ChildUpdatedEvent childUpdatedEvent)
      throws ImpossibleHistoryException {
    if (parentObjectDto == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to update a child");
    }
    ChildObjectDto childObjectDto =
        parentObjectDto.getChildren().stream()
            .filter(child -> child.getId().equals(childUpdatedEvent.getChildId()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ImpossibleHistoryException("Child object not found in history for update"));
    childObjectMapper.updateDto(childObjectDto, childUpdatedEvent.getChangeData());
    return parentObjectDto;
  }

  private ParentObjectDto applyEvent(
      ParentObjectDto parentObjectDto, ChildDeletedEvent childDeletedEvent)
      throws ImpossibleHistoryException {
    if (parentObjectDto == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to delete a child");
    }
    boolean removed =
        parentObjectDto
            .getChildren()
            .removeIf(child -> child.getId().equals(childDeletedEvent.getChildId()));
    if (!removed) {
      throw new ImpossibleHistoryException("Child object not found in history for deletion");
    }
    return parentObjectDto;
  }

  private ParentObjectDto applyEvent(ParentObjectDto parentObjectDto, EventBase event)
      throws ImpossibleHistoryException {
    return switch (event.getChangeType()) {
      case ParentCreated -> applyEvent(parentObjectDto, (ParentCreatedEvent) event);
      case ParentUpdated -> applyEvent(parentObjectDto, (ParentUpdatedEvent) event);
      case ParentDeleted -> applyEvent(parentObjectDto);
      case ChildCreated -> applyEvent(parentObjectDto, (ChildCreatedEvent) event);
      case ChildUpdated -> applyEvent(parentObjectDto, (ChildUpdatedEvent) event);
      case ChildDeleted -> applyEvent(parentObjectDto, (ChildDeletedEvent) event);
    };
  }

  private ParentDeletedEvent createReverseEvent(ParentCreatedEvent event) {
    return new ParentDeletedEvent(event.getId());
  }

  private ParentUpdatedEvent createReverseEvent(
      ParentObjectDto beforeState, ParentUpdatedEvent event) {
    return new ParentUpdatedEvent(new ParentObjectUpdateDto(beforeState.getName()), event.getId());
  }

  private ParentCreatedEvent createReverseEvent(
      ParentObjectDto beforeState, ParentDeletedEvent event) {
    return new ParentCreatedEvent(beforeState, event.getId());
  }

  private ChildDeletedEvent createReverseEvent(ChildCreatedEvent event) {
    return new ChildDeletedEvent(event.getChildId(), event.getId());
  }

  private ChildUpdatedEvent createReverseEvent(ParentObjectDto beforeState, ChildUpdatedEvent event)
      throws ImpossibleHistoryException {
    ChildObjectDto childBeforeState =
        beforeState.getChildren().stream()
            .filter(child -> child.getId().equals(event.getChildId()))
            .findFirst()
            .orElseThrow(
                () -> new ImpossibleHistoryException("Child object not found for update reversal"));
    return new ChildUpdatedEvent(
        event.getChildId(),
        new ChildObjectUpdateDto(
            childBeforeState.getName(), -event.getChangeData().getValueChange()),
        event.getId());
  }

  private ChildCreatedEvent createReverseEvent(ParentObjectDto beforeState, ChildDeletedEvent event)
      throws ImpossibleHistoryException {
    ChildObjectDto childBeforeState =
        beforeState.getChildren().stream()
            .filter(child -> child.getId().equals(event.getChildId()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ImpossibleHistoryException("Child object not found for creation reversal"));
    return new ChildCreatedEvent(event.getChildId(), childBeforeState, event.getId());
  }

  private EventBase createReverseEvent(ParentObjectDto beforeState, EventBase event)
      throws ImpossibleHistoryException {
    EventBase newEvent =
        switch (event.getChangeType()) {
          case ParentCreated -> createReverseEvent((ParentCreatedEvent) event);
          case ParentUpdated -> createReverseEvent(beforeState, (ParentUpdatedEvent) event);
          case ParentDeleted -> createReverseEvent(beforeState, (ParentDeletedEvent) event);
          case ChildCreated -> createReverseEvent((ChildCreatedEvent) event);
          case ChildUpdated -> createReverseEvent(beforeState, (ChildUpdatedEvent) event);
          case ChildDeleted -> createReverseEvent(beforeState, (ChildDeletedEvent) event);
        };
    newEvent.setTimestamp(LocalDateTime.now());
    newEvent.setId(UUID.randomUUID().toString());
    return newEvent;
  }

  @Override
  public ParentObjectDto constructParentFromEvents(List<EventBase> events)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parent = null;

    for (EventBase event : events) {
      parent = applyEvent(parent, event);
    }

    return parent;
  }

  @Override
  public ParentObjectDto constructParentFromEventsUpToHistoryId(
      List<EventBase> events, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parent = null;

    for (EventBase event : events) {
      parent = applyEvent(parent, event);

      if (event.getId().equals(targetHistoryId)) {
        return parent;
      }
    }

    throw new ResourceNotFoundException("History entry not found with ID: " + targetHistoryId);
  }

  @Override
  public ParentObjectDto restoreParentToHistoryEntry(
      List<EventBase> events, String targetHistoryId, List<EventBase> newEventsToApply)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    List<EventBase> reverseEvents = new ArrayList<>();
    ParentObjectDto parent = null;
    boolean alreadyEncounteredTarget = false;

    // Reconstruct the parent object using the history and generate reverse events on the way
    for (EventBase event : events) {
      if (alreadyEncounteredTarget) {
        reverseEvents.add(createReverseEvent(parent, event));
      }
      parent = applyEvent(parent, event);
      if (event.getId().equals(targetHistoryId)) {
        alreadyEncounteredTarget = true;
      }
    }

    // If we never encountered the target history ID, throw an exception
    if (!alreadyEncounteredTarget) {
      throw new ResourceNotFoundException("History entry not found with ID: " + targetHistoryId);
    }

    // Apply the reverse events in reverse order
    for (EventBase event : reverseEvents.reversed()) {
      parent = applyEvent(parent, event);
      newEventsToApply.add(event);
    }

    return parent;
  }
}
