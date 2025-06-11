/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ChangeType;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
public abstract class EventBase {
  @Id private String id;

  @Field(name = "reverts_history_id")
  private String revertsHistoryId;

  private LocalDateTime timestamp;

  @Field(name = "change_type", targetType = FieldType.STRING)
  private ChangeType changeType;
}
