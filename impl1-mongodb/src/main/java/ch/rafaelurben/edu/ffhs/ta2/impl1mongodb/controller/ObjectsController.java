/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.controller;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service.ObjectService;
import ch.rafaelurben.edu.ffhs.ta2.server.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ObjectsController implements ObjectsApi {
  private final ObjectService objectService;

  @Override
  public ResponseEntity<ParentObjectDto> createParent(
      @Valid ParentObjectCreateDto parentObjectCreateDto) {
    ParentObjectDto createdObject = objectService.createParentObject(parentObjectCreateDto);
    return new ResponseEntity<>(createdObject, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<ParentObjectDto>> getAllParents() {
    List<ParentObjectDto> parentObjects = objectService.getAllParentObjects();
    return new ResponseEntity<>(parentObjects, HttpStatus.OK);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<ParentObjectDto> getParent(String parentId) {
    ParentObjectDto parentObject = objectService.getParentObjectById(parentId);
    return new ResponseEntity<>(parentObject, HttpStatus.OK);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<ParentObjectDto> updateParent(
      String parentId, @Valid ParentObjectUpdateDto parentObjectUpdateDto) {
    ParentObjectDto updatedObject =
        objectService.updateParentObject(parentId, parentObjectUpdateDto);
    return new ResponseEntity<>(updatedObject, HttpStatus.OK);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<Void> deleteParent(String parentId) {
    objectService.deleteParentObject(parentId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<ChildObjectDto> createChild(
      String parentId, @Valid ChildObjectCreateDto childObjectCreateDto) {
    ChildObjectDto createdChild = objectService.createChildObject(parentId, childObjectCreateDto);
    return new ResponseEntity<>(createdChild, HttpStatus.CREATED);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<ChildObjectDto> getChild(String parentId, String childId) {
    ChildObjectDto childObject = objectService.getChildObjectById(parentId, childId);
    return new ResponseEntity<>(childObject, HttpStatus.OK);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<ChildObjectDto> updateChild(
      String parentId, String childId, @Valid ChildObjectUpdateDto childObjectUpdateDto) {
    ChildObjectDto updatedChild =
        objectService.updateChildObjectById(parentId, childId, childObjectUpdateDto);
    return new ResponseEntity<>(updatedChild, HttpStatus.OK);
  }

  @Override
  @SneakyThrows(ResourceNotFoundException.class)
  public ResponseEntity<Void> deleteChild(String parentId, String childId) {
    objectService.deleteChildObjectById(parentId, childId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
