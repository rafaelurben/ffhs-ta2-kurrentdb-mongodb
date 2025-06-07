/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObject;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectCreateDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectUpdateDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ChildObjectMapper.class})
public interface ParentObjectMapper {
  ParentObjectDto toDto(ParentObject parentObject);

  List<ParentObjectDto> toDto(List<ParentObject> parentObjects);

  ParentObject toEntity(ParentObjectCreateDto parentObjectCreateDto);

  void updateEntity(
      @MappingTarget ParentObject parentObject, ParentObjectUpdateDto parentObjectUpdateDto);
}
