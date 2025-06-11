/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChildCreatedEvent extends EventBase {
  private String childId;

  private ChildObjectDto createData;

  @Builder
  public ChildCreatedEvent(String childId, ChildObjectDto createData, String revertsHistoryId) {
    super();
    this.childId = childId;
    this.createData = createData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.CHILD_CREATED);
  }
}
