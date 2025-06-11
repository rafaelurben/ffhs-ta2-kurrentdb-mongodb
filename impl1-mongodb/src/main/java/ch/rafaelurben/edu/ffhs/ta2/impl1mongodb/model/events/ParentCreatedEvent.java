/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParentCreatedEvent extends EventBase {
  @Field(name = "create_data")
  private ParentObjectDto createData;

  @Builder
  public ParentCreatedEvent(ParentObjectDto createData, String revertsHistoryId) {
    super();
    this.createData = createData;
    this.setRevertsHistoryId(revertsHistoryId);
    this.setChangeType(ChangeType.PARENT_CREATED);
  }
}
