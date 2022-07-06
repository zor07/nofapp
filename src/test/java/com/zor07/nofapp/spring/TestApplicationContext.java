package com.zor07.nofapp.spring;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.spring.s3.S3TestContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@Profile("test")
public class TestApplicationContext {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @ConditionalOnProperty(prefix = "config", name = "testContainers", havingValue = "true", matchIfMissing = true)
  S3TestContainer s3TestContainer() {
    return new S3TestContainer();
  }
  @Bean
  S3Service s3Service(final ApplicationConfig config,
                      final Optional<S3TestContainer> s3TestContainer) {
    if (s3TestContainer.isEmpty()) {
      return new S3Service(config.getS3());
    } else {
      final var cfg = new S3Service.Config();
      cfg.setEndpoint(s3TestContainer.get().getEndpoint());
      cfg.setAccessKey(s3TestContainer.get().getAccessKey());
      cfg.setSecretKey(s3TestContainer.get().getSecretKey());
      cfg.setRegion(s3TestContainer.get().getRegion());
      cfg.setAuto(s3TestContainer.get().isAuto());
      return new S3Service(cfg);
    }
  }

}
