package com.zor07.nofapp.security;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final ObjectMapper objectMapper;

  @Autowired
  public SecurityConfig(final UserDetailsService userDetailsService,
      final PasswordEncoder passwordEncoder,
      final ObjectMapper objectMapper) {
    this.userDetailsService = userDetailsService;
    this.bCryptPasswordEncoder = passwordEncoder;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    final var filter = new CustomAuthenticationFilter(authenticationManagerBean(), objectMapper);
    filter.setFilterProcessesUrl("/api/v1/auth/login");
    http.cors();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests().antMatchers("/api/v1/auth/login/**", "/api/v1/auth/token/refresh/**").permitAll();
    http.authorizeRequests().antMatchers("/api/**").hasAnyAuthority("ROLE_USER");
    http.authorizeRequests().anyRequest().authenticated();
    http.addFilter(filter);
    http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
    configuration.setAllowedMethods(ImmutableList.of("HEAD",
        "GET", "POST", "PUT", "DELETE", "PATCH"));
    // setAllowCredentials(true) is important, otherwise:
    // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
    configuration.setAllowCredentials(true);
    // setAllowedHeaders is important! Without it, OPTIONS preflight request
    // will fail with 403 Invalid CORS request
    configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
      }
    };
  }

}
