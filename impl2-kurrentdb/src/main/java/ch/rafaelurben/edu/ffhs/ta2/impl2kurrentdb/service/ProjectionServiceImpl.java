/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.config.KurrentDBConfig;
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
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

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
    } catch (ExecutionException | InterruptedException ex) {
      if (ex.getMessage().contains("Conflict")) {
        System.out.println(
            "Projection " + projectionName + " already exists, trying to update it.");

        try {
          kurrentDBProjectionManagementClient.update(projectionName, projectionCode).get();
          System.out.println("Projection " + projectionName + " updated successfully.");
        } catch (ExecutionException | InterruptedException e) {
          System.err.println("Failed to update projection: " + projectionName);
          throw new RuntimeException("Failed to update projection: " + projectionName, e);
        }
      } else {
        System.err.println("Failed to create projection: " + projectionName);
        throw new RuntimeException("Failed to create or update projection: " + projectionName, ex);
      }
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
    } catch (Exception e) {
      throw new RuntimeException("Failed to read all parents from projection", e);
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
    } catch (Exception e) {
      throw new RuntimeException("Failed to read parent object from projection: " + parentId, e);
    }
  }
}
