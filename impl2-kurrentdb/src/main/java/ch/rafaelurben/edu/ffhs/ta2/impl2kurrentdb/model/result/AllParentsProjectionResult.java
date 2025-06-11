/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.result;

import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import java.util.List;

public record AllParentsProjectionResult(List<ParentObjectDto> parents) {}
