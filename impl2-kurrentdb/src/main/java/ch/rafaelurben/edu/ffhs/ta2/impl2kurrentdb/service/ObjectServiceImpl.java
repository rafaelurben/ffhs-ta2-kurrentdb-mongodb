/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ImpossibleHistoryException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper.ChildObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper.EventMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper.ParentObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events.*;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectServiceImpl implements ObjectService {

  private final ParentObjectMapper parentObjectMapper;
  private final ChildObjectMapper childObjectMapper;
  private final EventMapper eventMapper;

  private final StreamService streamService;
  private final ProjectionService projectionService;
  private final AggregateService aggregateService;

  private ParentObjectDto getParentObjectFromStream(String parentId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    List<EventBase> events = streamService.readObjectStream(parentId);
    ParentObjectDto parentObject = aggregateService.constructParentFromEvents(events);
    if (parentObject == null) {
      throw new ResourceNotFoundException("Parent object already deleted");
    }
    return parentObject;
  }

  private ParentObjectDto getParentObjectFromProjection(String parentId)
      throws ResourceNotFoundException {
    ParentObjectDto parentObject = projectionService.readParentFromProjection(parentId);
    if (parentObject == null) {
      throw new ResourceNotFoundException("Parent object not found in projection");
    }
    return parentObject;
  }

  private ChildObjectDto getChildObject(ParentObjectDto parentObject, String childId)
      throws ResourceNotFoundException {
    return parentObject.getChildren().stream()
        .filter(child -> child.getId().equals(childId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Child object not found"));
  }

  @Override
  public List<ParentObjectDto> getAllParentObjects() {
    return projectionService.readAllParentsFromProjection();
  }

  @Override
  public ParentObjectDto createParentObject(ParentObjectCreateDto parentObjectCreateDto) {
    String parentId = UUID.randomUUID().toString();
    ParentObjectDto parentObject = parentObjectMapper.toDto(parentObjectCreateDto);
    parentObject.setId(parentId);

    // Create and store event
    EventBase event = ParentCreatedEvent.builder().createData(parentObject).build();
    streamService.appendEvent(parentId, event, StreamService.expectNoStream());

    return parentObject;
  }

  @Override
  public ParentObjectDto getParentObjectById(String parentId) throws ResourceNotFoundException {
    return getParentObjectFromProjection(parentId);
  }

  @Override
  public ParentObjectDto updateParentObject(
      String parentId, ParentObjectUpdateDto parentObjectUpdateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parentObject = getParentObjectFromStream(parentId);
    parentObjectMapper.updateDto(parentObject, parentObjectUpdateDto);

    // Create and store event
    EventBase event = ParentUpdatedEvent.builder().changeData(parentObjectUpdateDto).build();
    streamService.appendEvent(parentId, event);

    return parentObject;
  }

  @Override
  public void deleteParentObject(String parentId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    getParentObjectFromStream(parentId);

    // Create and store event
    EventBase event = ParentDeletedEvent.builder().build();
    streamService.appendEvent(parentId, event);
  }

  @Override
  public ChildObjectDto createChildObject(
      String parentId, ChildObjectCreateDto childObjectCreateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parentObject = getParentObjectFromStream(parentId);
    ChildObjectDto childObject = childObjectMapper.toDto(childObjectCreateDto);
    childObject.setId(UUID.randomUUID().toString());
    parentObject.getChildren().add(childObject);

    // Create and store event
    EventBase childCreatedEvent =
        ChildCreatedEvent.builder().childId(childObject.getId()).createData(childObject).build();
    streamService.appendEvent(parentId, childCreatedEvent);

    return childObject;
  }

  @Override
  public ChildObjectDto getChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException {
    ParentObjectDto parentObject = getParentObjectFromProjection(parentId);
    return getChildObject(parentObject, childId);
  }

  @Override
  public ChildObjectDto updateChildObjectById(
      String parentId, String childId, ChildObjectUpdateDto childObjectUpdateDto)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parentObject = getParentObjectFromStream(parentId);
    ChildObjectDto childObject = getChildObject(parentObject, childId);
    childObjectMapper.updateDto(childObject, childObjectUpdateDto);

    // Create and store event
    EventBase childUpdatedEvent =
        ChildUpdatedEvent.builder()
            .childId(childObject.getId())
            .changeData(childObjectUpdateDto)
            .build();
    streamService.appendEvent(parentId, childUpdatedEvent);

    return childObject;
  }

  @Override
  public void deleteChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    ParentObjectDto parentObject = getParentObjectFromStream(parentId);
    ChildObjectDto childObject = getChildObject(parentObject, childId);
    parentObject.getChildren().remove(childObject);

    // Create and store event
    EventBase childDeletedEvent = ChildDeletedEvent.builder().childId(childObject.getId()).build();
    streamService.appendEvent(parentId, childDeletedEvent);
  }

  @Override
  public List<HistoryEntryDto> getAllHistoryEntriesByParentId(String parentId)
      throws ResourceNotFoundException {
    List<EventBase> events = streamService.readObjectStream(parentId);
    return eventMapper.toDto(events);
  }

  @Override
  public ParentObjectDto previewParentAtHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    return aggregateService.constructParentFromEventsUpToHistoryId(
        streamService.readObjectStream(parentId), historyId);
  }

  @Override
  public ParentObjectDto restoreParentToHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException, ImpossibleHistoryException {
    List<EventBase> newEvents = new ArrayList<>();
    ParentObjectDto result =
        aggregateService.restoreParentToHistoryEntry(
            streamService.readObjectStream(parentId), historyId, newEvents);
    streamService.appendEvents(parentId, newEvents);
    return result;
  }
}
