/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectUpdateDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChildUpdatedEvent extends EventBase {
  private String childId;

  private ChildObjectUpdateDto changeData;

  @Builder
  public ChildUpdatedEvent(
      String childId, ChildObjectUpdateDto changeData, String revertsHistoryId) {
    super();
    this.childId = childId;
    this.changeData = changeData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.CHILD_UPDATED);
  }
}
