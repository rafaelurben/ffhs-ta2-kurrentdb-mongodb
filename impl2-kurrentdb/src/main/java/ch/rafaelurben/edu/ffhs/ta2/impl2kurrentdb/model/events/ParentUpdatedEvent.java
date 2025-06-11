/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectUpdateDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParentUpdatedEvent extends EventBase {
  private ParentObjectUpdateDto changeData;

  @Builder
  public ParentUpdatedEvent(ParentObjectUpdateDto changeData, String revertsHistoryId) {
    super();
    this.changeData = changeData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.PARENT_UPDATED);
  }
}
