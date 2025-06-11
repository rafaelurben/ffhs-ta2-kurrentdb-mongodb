/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.config;

import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBConnectionString;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurrentDBConfig {
  public static final String STREAM_PREFIX = "ta2object";
  public static final String PROJECTION_NAME_PER_PARENT = "ta2-per-object-projection";
  public static final String PROJECTION_NAME_ALL_PARENTS = "ta2-all-objects-projection";

  @Value("${kurrentdb.connectionString}")
  String connectionString;

  @Bean
  public KurrentDBClient EventStoreDBClient() {
    return KurrentDBClient.create(KurrentDBConnectionString.parseOrThrow(connectionString));
  }

  @Bean
  public KurrentDBProjectionManagementClient ProjectionManagementClient(
      KurrentDBClient kurrentDBClient) {
    return KurrentDBProjectionManagementClient.from(kurrentDBClient);
  }

  public String getStreamName(String parentId) {
    return STREAM_PREFIX + "-" + parentId;
  }
}
