/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParentCreatedEvent extends EventBase {
  private ParentObjectDto createData;

  @Builder
  public ParentCreatedEvent(ParentObjectDto createData, String revertsHistoryId) {
    super();
    this.createData = createData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.PARENT_CREATED);
  }
}
