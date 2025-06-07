/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.mapper;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ChildCreatedEvent;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ChildDeletedEvent;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ChildUpdatedEvent;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.EventBase;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ParentCreatedEvent;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ParentDeletedEvent;
import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.ParentUpdatedEvent;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildCreatedEventDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildDeletedEventDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildUpdatedEventDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.HistoryEntryDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentCreatedEventDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentDeletedEventDto;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentUpdatedEventDto;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
  @Named("mapSingleEvent")
  default HistoryEntryDto toDto(EventBase event) {
    return switch (event.getChangeType()) {
      case ParentCreated -> toDto((ParentCreatedEvent) event);
      case ParentUpdated -> toDto((ParentUpdatedEvent) event);
      case ParentDeleted -> toDto((ParentDeletedEvent) event);
      case ChildCreated -> toDto((ChildCreatedEvent) event);
      case ChildUpdated -> toDto((ChildUpdatedEvent) event);
      case ChildDeleted -> toDto((ChildDeletedEvent) event);
    };
  }

  @IterableMapping(qualifiedByName = "mapSingleEvent")
  List<HistoryEntryDto> toDto(List<EventBase> events);

  ParentCreatedEventDto toDto(ParentCreatedEvent event);

  ParentUpdatedEventDto toDto(ParentUpdatedEvent event);

  ParentDeletedEventDto toDto(ParentDeletedEvent event);

  ChildCreatedEventDto toDto(ChildCreatedEvent event);

  ChildUpdatedEventDto toDto(ChildUpdatedEvent event);

  ChildDeletedEventDto toDto(ChildDeletedEvent event);
}
