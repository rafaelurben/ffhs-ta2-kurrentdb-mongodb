/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.config.KurrentDBConfig;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions.KurrentException;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.result.AllParentsProjectionResult;
import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.model.result.SingleParentProjectionResult;
import ch.rafaelurben.edu.ffhs.ta2.server.model.ParentObjectDto;
import io.kurrent.dbclient.GetProjectionResultOptions;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectionServiceImpl implements ProjectionService {
  private final KurrentDBProjectionManagementClient projectionManagementClient;
  private final KurrentDBProjectionManagementClient kurrentDBProjectionManagementClient;

  @Value("classpath:projections/allParentsProjectionPreset.js")
  private Resource allParentsProjectionPresetResource;

  @Value("classpath:projections/perParentProjectionPreset.js")
  private Resource perParentProjectionPresetResource;

  private String readResourceContent(Resource resource) {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void createOrUpdateProjection(String projectionName, String projectionCode) {
    try {
      kurrentDBProjectionManagementClient.create(projectionName, projectionCode).get();
      kurrentDBProjectionManagementClient.enable(projectionName).get();
    } catch (ExecutionException ex) {
      if (ex.getMessage().contains("Conflict")) {
        log.info("Projection {} already exists, trying to update it.", projectionName);

        try {
          kurrentDBProjectionManagementClient.update(projectionName, projectionCode).get();
          log.info("Projection {} updated successfully.", projectionName);
        } catch (ExecutionException e) {
          log.error("Failed to update projection: {}", projectionName);
          throw new KurrentException("Failed to update projection: " + projectionName, e);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.error("Projection update interrupted: {}", projectionName);
          throw new KurrentException("Projection update interrupted: " + projectionName, e);
        }
      } else {
        log.error("Failed to create projection: {}", projectionName);
        throw new KurrentException("Failed to create or update projection: " + projectionName, ex);
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      log.error("Projection creation interrupted: {}", projectionName);
      throw new KurrentException("Projection creation interrupted: " + projectionName, ex);
    }
  }

  private void setupAllParentProjection() {
    String projectionName = KurrentDBConfig.PROJECTION_NAME_ALL_PARENTS;
    String projectionQuery =
        String.format(
            readResourceContent(allParentsProjectionPresetResource), KurrentDBConfig.STREAM_PREFIX);
    createOrUpdateProjection(projectionName, projectionQuery);
  }

  private void setupPerParentProjection() {
    String projectionName = KurrentDBConfig.PROJECTION_NAME_PER_PARENT;
    String projectionQuery =
        String.format(
            readResourceContent(perParentProjectionPresetResource), KurrentDBConfig.STREAM_PREFIX);
    createOrUpdateProjection(projectionName, projectionQuery);
  }

  public void setupProjections() {
    setupAllParentProjection();
    setupPerParentProjection();
  }

  public List<ParentObjectDto> readAllParentsFromProjection() {
    String projectionName = KurrentDBConfig.PROJECTION_NAME_ALL_PARENTS;
    try {
      AllParentsProjectionResult result =
          projectionManagementClient
              .getResult(projectionName, AllParentsProjectionResult.class)
              .get();
      return result.parents();
    } catch (CancellationException | ExecutionException e) {
      throw new KurrentException("Failed to read all parents from projection", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new KurrentException("Interrupted while reading all parents from projection", e);
    }
  }

  public ParentObjectDto readParentFromProjection(String parentId) {
    String projectionName = KurrentDBConfig.PROJECTION_NAME_PER_PARENT;
    try {
      SingleParentProjectionResult result =
          projectionManagementClient
              .getResult(
                  projectionName,
                  SingleParentProjectionResult.class,
                  GetProjectionResultOptions.get().partition(parentId))
              .get();
      return result.parent();
    } catch (CancellationException | ExecutionException e) {
      throw new KurrentException("Failed to read parent object from projection: " + parentId, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new KurrentException("Interrupted while reading all parents from projection", e);
    }
  }
}
