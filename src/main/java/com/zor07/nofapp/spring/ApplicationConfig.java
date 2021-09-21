package com.zor07.nofapp.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import com.zor07.nofapp.services.TimerService;

@Configuration
@ConfigurationProperties("config")
@Validated
public class ApplicationConfig {

  @NotNull
  @Valid
  private TimerService.Config timerConfig;

  public TimerService.Config getTimerConfig() {
    return timerConfig;
  }

  public void setTimerConfig(TimerService.Config timerConfig) {
    this.timerConfig = timerConfig;
  }
}
