/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

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
    this.setChangeType(ChangeType.ParentDeleted);
  }
}
