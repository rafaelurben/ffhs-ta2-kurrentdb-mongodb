/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChildDeletedEvent extends EventBase {
  @Field(name = "child_id")
  private String childId;

  @Builder
  public ChildDeletedEvent(String childId, String revertsHistoryId) {
    super();
    this.childId = childId;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.ChildDeleted);
  }
}
