/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ChildObject;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChildObjectMapper {
  ChildObjectDto toDto(ChildObject childObject);

  ChildObject toEntity(ChildObjectCreateDto childObjectCreateDto);

  default void updateEntity(ChildObject childObject, ChildObjectUpdateDto childObjectUpdateDto) {
    childObject.setName(childObjectUpdateDto.getName());
    childObject.setValue(childObject.getValue() + childObjectUpdateDto.getValueChange());
  }
}
