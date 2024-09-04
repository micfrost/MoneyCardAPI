package com.micfro.moneycard.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.authorizeHttpRequests(request -> request
            .requestMatchers("/moneycards/**")
            .hasRole("CARD-OWNER")) // enable RBAC
        .httpBasic(Customizer.withDefaults())     // Enables basic HTTP authentication
        .csrf(csrf -> csrf.disable());             // Disables Cross-Site Request Forgery (CSRF) protection
    return httpSecurity.build();                  // Builds the SecurityFilterChain object and returns it
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
    User.UserBuilder users = User.builder();
    UserDetails sarah = users
        .username("sarah1")
        .password(passwordEncoder.encode("abc123"))
        .roles("CARD-OWNER")
        .build();
    UserDetails hankOwnsNoCards = users
        .username("hank-owns-no-cards")
        .password(passwordEncoder.encode("qrs456"))
        .roles("NON-OWNER")
        .build();
    UserDetails kumar = users
        .username("kumar2")
        .password(passwordEncoder.encode("xyz789"))
        .roles("CARD-OWNER")
        .build();
    return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards, kumar);
  }


}
