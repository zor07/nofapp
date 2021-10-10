package com.zor07.nofapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.zor07.nofapp.timer.TimerRepository;
import com.zor07.nofapp.timer.TimerService;

@Configuration
@Profile("!test")
public class ApplicationContext {

  @Bean
  public TimerRepository timerRepository(final ApplicationConfig config,
      final ObjectMapper objectMapper) {
    return new TimerRepository(config.getTimerConfig(), objectMapper);
  }

  @Bean
  public TimerService timerService(final TimerRepository repository) {
    return new TimerService(repository);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new ParameterNamesModule())
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule());
  }

}
