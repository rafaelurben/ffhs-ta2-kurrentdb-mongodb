/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChildObjectMapper {
  ChildObjectDto toDto(ChildObjectCreateDto childObjectCreateDto);

  default void updateDto(ChildObjectDto childObjectDto, ChildObjectUpdateDto childObjectUpdateDto) {
    childObjectDto.setName(childObjectUpdateDto.getName());
    childObjectDto.setValue(childObjectDto.getValue() + childObjectUpdateDto.getValueChange());
  }
}
