package com.zor07.nofapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.zor07.nofapp.services.TimerService;

@Configuration
@Profile("!test")
public class ApplicationContext {

  @Bean
  public TimerService timerService(final ApplicationConfig config) {
    return new TimerService(config.getTimerConfig());
  }

}
