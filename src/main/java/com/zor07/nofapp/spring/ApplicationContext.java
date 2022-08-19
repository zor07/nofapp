package com.zor07.nofapp.spring;

import com.zor07.nofapp.aws.s3.S3Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("!test")
@EnableTransactionManagement
public class ApplicationContext {


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  S3Service s3Service(final ApplicationConfig appConfig) {
    return new S3Service(appConfig.getS3());
  }

}
