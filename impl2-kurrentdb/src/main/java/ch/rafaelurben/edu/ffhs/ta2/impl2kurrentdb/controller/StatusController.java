/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.controller;

import ch.rafaelurben.edu.ffhs.ta2.server.api.StatusApi;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ConnectionTestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StatusController implements StatusApi {
  @Override
  public ResponseEntity<ConnectionTestResponseDto> testConnection() {
    return new ResponseEntity<>(
        new ConnectionTestResponseDto("Connection successful"), HttpStatus.OK);
  }
}
