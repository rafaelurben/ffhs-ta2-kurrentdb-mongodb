/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.controller;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service.ObjectService;
import ch.rafaelurben.edu.ffhs.ta2.server.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<ParentObjectDto> getParent(String parentId) {
    ParentObjectDto parentObject = objectService.getParentObjectById(parentId);
    return new ResponseEntity<>(parentObject, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ParentObjectDto> updateParent(
      String parentId, @Valid ParentObjectUpdateDto parentObjectUpdateDto) {
    ParentObjectDto updatedObject =
        objectService.updateParentObject(parentId, parentObjectUpdateDto);
    return new ResponseEntity<>(updatedObject, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteParent(String parentId) {
    objectService.deleteParentObject(parentId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<ChildObjectDto> createChild(
      String parentId, @Valid ChildObjectCreateDto childObjectCreateDto) {
    ChildObjectDto createdChild = objectService.createChildObject(parentId, childObjectCreateDto);
    return new ResponseEntity<>(createdChild, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<ChildObjectDto> getChild(String parentId, String childId) {
    ChildObjectDto childObject = objectService.getChildObjectById(parentId, childId);
    return new ResponseEntity<>(childObject, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ChildObjectDto> updateChild(
      String parentId, String childId, @Valid ChildObjectUpdateDto childObjectUpdateDto) {
    ChildObjectDto updatedChild =
        objectService.updateChildObjectById(parentId, childId, childObjectUpdateDto);
    return new ResponseEntity<>(updatedChild, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteChild(String parentId, String childId) {
    objectService.deleteChildObjectById(parentId, childId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
