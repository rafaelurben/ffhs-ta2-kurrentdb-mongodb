/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "_class")
@Data
public abstract class EventBase {
  @Id private String id;

  private String revertsHistoryId;

  private LocalDateTime timestamp;

  private ChangeType changeType;

  protected EventBase() {
    this.timestamp = LocalDateTime.now();
  }
}
