/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper.ChildObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper.ParentObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ChildObject;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObject;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObjectHistory;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.*;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectUpdateDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectUpdateDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryReconstructorServiceImpl implements HistoryReconstructorService {
  private final ParentObjectMapper parentObjectMapper;
  private final ChildObjectMapper childObjectMapper;

  private ParentObject applyEvent(ParentObject parentObject, ParentCreatedEvent parentCreatedEvent)
      throws ImpossibleHistoryException {
    if (parentObject != null) {
      throw new ImpossibleHistoryException(
          "Parent object is not null when trying to create parent");
    }
    return parentObjectMapper.toEntity(parentCreatedEvent.getCreateData());
  }

  private ParentObject applyEvent(ParentObject parentObject, ParentUpdatedEvent parentUpdatedEvent)
      throws ImpossibleHistoryException {
    if (parentObject == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to update parent");
    }
    parentObjectMapper.updateEntity(parentObject, parentUpdatedEvent.getChangeData());
    return parentObject;
  }

  private ParentObject applyEvent(ParentObject parentObject) throws ImpossibleHistoryException {
    if (parentObject == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to delete parent");
    }
    return null;
  }

  private ParentObject applyEvent(ParentObject parentObject, ChildCreatedEvent childCreatedEvent)
      throws ImpossibleHistoryException {
    if (parentObject == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to create a child");
    }
    ChildObject childObject = childObjectMapper.toEntity(childCreatedEvent.getCreateData());
    parentObject.getChildren().add(childObject);
    return parentObject;
  }

  private ParentObject applyEvent(ParentObject parentObject, ChildUpdatedEvent childUpdatedEvent)
      throws ImpossibleHistoryException {
    if (parentObject == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to update a child");
    }
    ChildObject childObject =
        parentObject.getChildren().stream()
            .filter(child -> child.getId().equals(childUpdatedEvent.getChildId()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ImpossibleHistoryException("Child object not found in history for update"));
    childObjectMapper.updateEntity(childObject, childUpdatedEvent.getChangeData());
    return parentObject;
  }

  private ParentObject applyEvent(ParentObject parentObject, ChildDeletedEvent childDeletedEvent)
      throws ImpossibleHistoryException {
    if (parentObject == null) {
      throw new ImpossibleHistoryException("Parent object is null when trying to delete a child");
    }
    boolean removed =
        parentObject
            .getChildren()
            .removeIf(child -> child.getId().equals(childDeletedEvent.getChildId()));
    if (!removed) {
      throw new ImpossibleHistoryException("Child object not found in history for deletion");
    }
    return parentObject;
  }

  private ParentObject applyEvent(ParentObject parentObject, EventBase event)
      throws ImpossibleHistoryException {
    return switch (event.getChangeType()) {
      case PARENT_CREATED -> applyEvent(parentObject, (ParentCreatedEvent) event);
      case PARENT_UPDATED -> applyEvent(parentObject, (ParentUpdatedEvent) event);
      case PARENT_DELETED -> applyEvent(parentObject);
      case CHILD_CREATED -> applyEvent(parentObject, (ChildCreatedEvent) event);
      case CHILD_UPDATED -> applyEvent(parentObject, (ChildUpdatedEvent) event);
      case CHILD_DELETED -> applyEvent(parentObject, (ChildDeletedEvent) event);
    };
  }

  private ParentDeletedEvent createReverseEvent(ParentCreatedEvent event) {
    return new ParentDeletedEvent(event.getId());
  }

  private ParentUpdatedEvent createReverseEvent(
      ParentObject beforeState, ParentUpdatedEvent event) {
    return new ParentUpdatedEvent(new ParentObjectUpdateDto(beforeState.getName()), event.getId());
  }

  private ParentCreatedEvent createReverseEvent(
      ParentObject beforeState, ParentDeletedEvent event) {
    return new ParentCreatedEvent(parentObjectMapper.toDto(beforeState), event.getId());
  }

  private ChildDeletedEvent createReverseEvent(ChildCreatedEvent event) {
    return new ChildDeletedEvent(event.getChildId(), event.getId());
  }

  private ChildUpdatedEvent createReverseEvent(ParentObject beforeState, ChildUpdatedEvent event)
      throws ImpossibleHistoryException {
    ChildObject childBeforeState =
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

  private ChildCreatedEvent createReverseEvent(ParentObject beforeState, ChildDeletedEvent event)
      throws ImpossibleHistoryException {
    ChildObject childBeforeState =
        beforeState.getChildren().stream()
            .filter(child -> child.getId().equals(event.getChildId()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ImpossibleHistoryException("Child object not found for creation reversal"));
    return new ChildCreatedEvent(
        event.getChildId(), childObjectMapper.toDto(childBeforeState), event.getId());
  }

  private EventBase createReverseEvent(ParentObject beforeState, EventBase event)
      throws ImpossibleHistoryException {
    EventBase newEvent =
        switch (event.getChangeType()) {
          case PARENT_CREATED -> createReverseEvent((ParentCreatedEvent) event);
          case PARENT_UPDATED -> createReverseEvent(beforeState, (ParentUpdatedEvent) event);
          case PARENT_DELETED -> createReverseEvent(beforeState, (ParentDeletedEvent) event);
          case CHILD_CREATED -> createReverseEvent((ChildCreatedEvent) event);
          case CHILD_UPDATED -> createReverseEvent(beforeState, (ChildUpdatedEvent) event);
          case CHILD_DELETED -> createReverseEvent(beforeState, (ChildDeletedEvent) event);
        };
    newEvent.setTimestamp(LocalDateTime.now());
    newEvent.setId(new ObjectId().toString());
    return newEvent;
  }

  @Override
  public ParentObject reconstructParentAtHistoryEntry(
      ParentObjectHistory history, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    List<EventBase> events = history.getEvents();
    ParentObject parent = null;

    // Find the target history entry
    for (EventBase event : events) {
      parent = applyEvent(parent, event);

      if (event.getId().equals(targetHistoryId)) {
        return parent;
      }
    }

    throw new ResourceNotFoundException("History entry not found with ID: " + targetHistoryId);
  }

  @Override
  public ParentObject restoreParentToHistoryEntry(
      ParentObjectHistory history, String targetHistoryId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    List<EventBase> events = history.getEvents();
    List<EventBase> reverseEvents = new ArrayList<>();
    ParentObject parent = null;
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
      events.add(event); // Add the reverse event to the history
    }

    return parent;
  }
}
