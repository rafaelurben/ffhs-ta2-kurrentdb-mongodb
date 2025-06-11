/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ChildObjectDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChildCreatedEvent extends EventBase {
  @Field(name = "child_id")
  private String childId;

  @Field(name = "create_data")
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
