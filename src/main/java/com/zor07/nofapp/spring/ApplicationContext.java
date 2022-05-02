package com.zor07.nofapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@Profile("!test")
public class ApplicationContext {


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.zor07.nofapp.api"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaData());
  }

  private ApiInfo metaData() {
    return new ApiInfoBuilder()
            .title("Nofapp REST API")
            .description("\"Nofapp backend REST API\"")
            .version("1.0.0")
            .contact(new Contact("Anzor Karmov", "https://github.com/zor07", "anzor.karmov@gmail.com"))
            .build();
  }

}
