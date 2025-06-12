/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.listeners;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service.ProjectionService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupListener {
  private final ProjectionService projectionService;

  @PostConstruct
  public void initProjections() {
    projectionService.setupProjections();
  }
}
