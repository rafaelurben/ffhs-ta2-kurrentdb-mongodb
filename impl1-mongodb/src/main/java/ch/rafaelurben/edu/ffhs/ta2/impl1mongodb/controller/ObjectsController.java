/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.controller;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.service.ObjectService;
import ch.rafaelurben.edu.ffhs.ta2.server.api.ObjectsApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ObjectsController implements ObjectsApi {
  ObjectService objectService;

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
    try {
      ParentObjectDto parentObject = objectService.getParentObjectById(parentId);
      return new ResponseEntity<>(parentObject, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<ParentObjectDto> updateParent(
      String parentId, @Valid ParentObjectUpdateDto parentObjectUpdateDto) {
    try {
      ParentObjectDto updatedObject =
          objectService.updateParentObject(parentId, parentObjectUpdateDto);
      return new ResponseEntity<>(updatedObject, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<Void> deleteParent(String parentId) {
    try {
      objectService.deleteParentObject(parentId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<ChildObjectDto> createChild(
      String parentId, @Valid ChildObjectCreateDto childObjectCreateDto) {
    try {
      ChildObjectDto createdChild = objectService.createChildObject(parentId, childObjectCreateDto);
      return new ResponseEntity<>(createdChild, HttpStatus.CREATED);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<ChildObjectDto> getChild(String parentId, String childId) {
    try {
      ChildObjectDto childObject = objectService.getChildObjectById(parentId, childId);
      return new ResponseEntity<>(childObject, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<ChildObjectDto> updateChild(
      String parentId, String childId, @Valid ChildObjectUpdateDto childObjectUpdateDto) {
    try {
      ChildObjectDto updatedChild =
          objectService.updateChildObjectById(parentId, childId, childObjectUpdateDto);
      return new ResponseEntity<>(updatedChild, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<Void> deleteChild(String parentId, String childId) {
    try {
      objectService.deleteChildObjectById(parentId, childId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
