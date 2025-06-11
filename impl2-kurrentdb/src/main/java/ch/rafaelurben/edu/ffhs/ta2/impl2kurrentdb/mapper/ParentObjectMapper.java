/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.mapper;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ChildObjectMapper.class})
public interface ParentObjectMapper {
  ParentObjectDto toDto(ParentObjectCreateDto parentObjectCreateDto);

  void updateDto(
      @MappingTarget ParentObjectDto parentObject, ParentObjectUpdateDto parentObjectUpdateDto);
}
