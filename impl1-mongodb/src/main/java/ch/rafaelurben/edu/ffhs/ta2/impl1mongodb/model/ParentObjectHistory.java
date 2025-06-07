/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.events.EventBase;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "object_history")
public class ParentObjectHistory {
  @Id private String parentObjectId;

  private List<EventBase> events = new ArrayList<>();

  public ParentObjectHistory(String parentObjectId) {
    this.parentObjectId = parentObjectId;
    this.events = new ArrayList<>();
  }

  public void addEvent(EventBase event) {
    this.events.add(event);
  }
}
