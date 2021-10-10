package com.zor07.nofapp.timer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimerRepository {

  public static class Config {

    private String dataPath;

    public String getDataPath() {
      return dataPath;
    }

    public void setDataPath(String dataPath) {
      this.dataPath = dataPath;
    }
  }

  private static Map<Integer, Timer> DATA_MAP;

  private final String dataPath;
  private final ObjectMapper objectMapper;


  public TimerRepository(final Config config,
      final ObjectMapper objectMapper) {
    this.dataPath = config.dataPath;
    this.objectMapper = objectMapper;
    load();
  }

  public List<Timer> getTimers() {
    return new ArrayList<>(DATA_MAP.values());
  }

  public void persist(final Timer timer) {
    final var id = DATA_MAP.keySet().stream()
        .max(Integer::compare)
        .orElse(0) + 1;
    timer.id = id;
    DATA_MAP.put(id, timer);
    save();
  }

  private void load() {
    try (var is = getClass().getClassLoader().getResourceAsStream(dataPath)) {
      var data = objectMapper.readValue(is, Timers.class);
      DATA_MAP = data.timers().stream()
          .collect(Collectors.toMap(t -> t.id, t -> t));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void save() {
    try {
      final var resource = getClass().getClassLoader().getResource(dataPath);
      final var file = new File(resource.toURI());
      objectMapper.writeValue(file, new Timers(new ArrayList<>(DATA_MAP.values())));
    } catch (final Exception e) {
      throw new RuntimeException();
    }
  }
}
