package com.zor07.nofapp.spring.s3;

import org.testcontainers.containers.GenericContainer;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class S3TestContainer implements AutoCloseable {

  private static GenericContainer<?> createContainer(final int containerPort, final String accessKey, final String secretKey) {
    final var container = new GenericContainer<>("minio/minio:latest");
    container.setCommandParts(new String[] {
      "server",
      "/data" });
    container.withEnv("MALLOC_ARENA_MAX", "1");
    container.withEnv("MINIO_ROOT_USER", accessKey);
    container.withEnv("MINIO_ROOT_PASSWORD", secretKey);
    container.withExposedPorts(containerPort);
    container.start();
    return container;
  }

  private final GenericContainer<?> container;

  private final String accessKey;

  private final String secretKey;

  private final String region;

  private final String endpoint;

  private final boolean auto;

  public S3TestContainer() {
    accessKey = "admin";
    secretKey = "adminadmin";
    region = "eu-central-1";
    final int containerPort = 9000;
    container = createContainer(containerPort, accessKey, secretKey);
    final var host = container.getContainerIpAddress();
    final var port = container.getMappedPort(containerPort);
    endpoint = String.format("http://%s:%s", host, port);
    auto = false;
  }

  @Override
  public void close() {
    try {
      container.close();
    } catch (Exception e) {
      // ignore
    }
  }

  public String getAccessKey() {
    return accessKey;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getRegion() {
    return region;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public boolean isAuto() {
    return auto;
  }

}
