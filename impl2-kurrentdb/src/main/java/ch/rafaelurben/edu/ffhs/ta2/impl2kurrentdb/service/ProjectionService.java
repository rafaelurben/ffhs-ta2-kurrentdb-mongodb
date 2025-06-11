/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.ResourceNotFoundException;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import java.util.List;

public interface ProjectionService {
  void setupProjections();

  List<ParentObjectDto> readAllParentsFromProjection();

  ParentObjectDto readParentFromProjection(String parentId) throws ResourceNotFoundException;
}
