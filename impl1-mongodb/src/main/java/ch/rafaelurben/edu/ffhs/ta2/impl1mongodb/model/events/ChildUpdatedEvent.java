/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectUpdateDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChildUpdatedEvent extends EventBase {
  @Field(name = "child_id")
  private String childId;

  @Field(name = "update_data")
  private ChildObjectUpdateDto changeData;

  @Builder
  public ChildUpdatedEvent(
      String childId, ChildObjectUpdateDto changeData, String revertsHistoryId) {
    super();
    this.childId = childId;
    this.changeData = changeData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.ChildUpdated);
  }
}
