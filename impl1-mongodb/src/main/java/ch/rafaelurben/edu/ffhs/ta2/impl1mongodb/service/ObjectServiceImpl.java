/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper.ChildObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper.EventMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper.ParentObjectMapper;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ChildObject;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObject;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObjectHistory;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.*;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.repository.ParentObjectHistoryRepository;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.repository.ParentObjectRepository;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ObjectServiceImpl implements ObjectService {
  private final ParentObjectRepository objectRepository;
  private final ParentObjectHistoryRepository historyRepository;

  private final ParentObjectMapper parentObjectMapper;
  private final ChildObjectMapper childObjectMapper;
  private final EventMapper eventMapper;

  private String generateId() {
    return new ObjectId().toString();
  }

  private void storeEvent(String parentId, EventBase event) {
    ParentObjectHistory history =
        historyRepository
            .findByParentObjectId(parentId)
            .orElseGet(() -> new ParentObjectHistory(parentId));
    event.setTimestamp(LocalDateTime.now());
    event.setId(generateId());
    history.addEvent(event);
    historyRepository.save(history);
  }

  private ParentObject getParentObject(String parentId) throws ResourceNotFoundException {
    return objectRepository
        .findById(parentId)
        .orElseThrow(() -> new ResourceNotFoundException("Parent object not found"));
  }

  private ChildObject getChildObject(ParentObject parentObject, String childId)
      throws ResourceNotFoundException {
    return parentObject.getChildren().stream()
        .filter(child -> child.getId().equals(childId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Child object not found"));
  }

  @Override
  public List<ParentObjectDto> getAllParentObjects() {
    List<ParentObject> parentObjects = objectRepository.findAll();
    return parentObjectMapper.toDto(parentObjects);
  }

  @Override
  public ParentObjectDto createParentObject(ParentObjectCreateDto parentObjectCreateDto) {
    ParentObject parentObject = parentObjectMapper.toEntity(parentObjectCreateDto);
    parentObject = objectRepository.save(parentObject);
    ParentObjectDto parentObjectDto = parentObjectMapper.toDto(parentObject);

    // Create history entry
    EventBase parentCreatedEvent = ParentCreatedEvent.builder().createData(parentObjectDto).build();
    storeEvent(parentObject.getId(), parentCreatedEvent);

    return parentObjectDto;
  }

  @Override
  public ParentObjectDto getParentObjectById(String parentId) throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    return parentObjectMapper.toDto(parentObject);
  }

  @Override
  public ParentObjectDto updateParentObject(
      String parentId, ParentObjectUpdateDto parentObjectUpdateDto)
      throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    parentObjectMapper.updateEntity(parentObject, parentObjectUpdateDto);
    objectRepository.save(parentObject);
    ParentObjectDto parentObjectDto = parentObjectMapper.toDto(parentObject);

    // Create history entry
    EventBase parentUpdatedEvent =
        ParentUpdatedEvent.builder().changeData(parentObjectUpdateDto).build();
    storeEvent(parentId, parentUpdatedEvent);

    return parentObjectDto;
  }

  @Override
  public void deleteParentObject(String parentId) throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    objectRepository.delete(parentObject);

    // Create history entry
    EventBase parentDeletedEvent = ParentDeletedEvent.builder().build();
    storeEvent(parentId, parentDeletedEvent);
  }

  @Override
  public ChildObjectDto createChildObject(
      String parentId, ChildObjectCreateDto childObjectCreateDto) throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    ChildObject childObject = childObjectMapper.toEntity(childObjectCreateDto);
    childObject.setId(generateId());
    parentObject.getChildren().add(childObject);
    objectRepository.save(parentObject);
    ChildObjectDto childObjectDto = childObjectMapper.toDto(childObject);

    // Create history entry
    EventBase childCreatedEvent =
        ChildCreatedEvent.builder()
            .childId(childObjectDto.getId())
            .createData(childObjectDto)
            .build();
    storeEvent(parentId, childCreatedEvent);

    return childObjectDto;
  }

  @Override
  public ChildObjectDto getChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    ChildObject childObject = getChildObject(parentObject, childId);
    return childObjectMapper.toDto(childObject);
  }

  @Override
  public ChildObjectDto updateChildObjectById(
      String parentId, String childId, ChildObjectUpdateDto childObjectUpdateDto)
      throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    ChildObject childObject = getChildObject(parentObject, childId);
    childObjectMapper.updateEntity(childObject, childObjectUpdateDto);
    objectRepository.save(parentObject);

    // Create history entry
    EventBase childUpdatedEvent =
        ChildUpdatedEvent.builder()
            .childId(childObject.getId())
            .changeData(childObjectUpdateDto)
            .build();
    storeEvent(parentId, childUpdatedEvent);

    return childObjectMapper.toDto(childObject);
  }

  @Override
  public void deleteChildObjectById(String parentId, String childId)
      throws ResourceNotFoundException {
    ParentObject parentObject = getParentObject(parentId);
    ChildObject childObject = getChildObject(parentObject, childId);
    parentObject.getChildren().remove(childObject);
    objectRepository.save(parentObject);

    // Create history entry
    EventBase childDeletedEvent = ChildDeletedEvent.builder().childId(childObject.getId()).build();
    storeEvent(parentId, childDeletedEvent);
  }

  // Object History

  @Override
  public List<HistoryEntryDto> getAllHistoryEntriesByParentId(String parentId)
      throws ResourceNotFoundException {
    ParentObjectHistory history =
        historyRepository
            .findByParentObjectId(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent object history not found"));
    return eventMapper.toDto(history.getEvents());
  }

  @Override
  public ParentObjectDto previewParentAtHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException {
    // TODO
    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  public ParentObjectDto restoreParentToHistoryEntry(String parentId, String historyId)
      throws ResourceNotFoundException {
    // TODO
    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
  }
}
