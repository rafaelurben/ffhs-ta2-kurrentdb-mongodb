/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParentDeletedEvent extends EventBase {
  @Builder
  public ParentDeletedEvent(String revertsHistoryId) {
    super();
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.PARENT_DELETED);
  }
}
