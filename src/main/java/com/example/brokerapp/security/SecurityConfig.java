package com.example.brokerapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req -> req.requestMatchers("/orders/create/**").hasRole("USER"))
             .authorizeHttpRequests(req -> req.requestMatchers("/orders/list").hasRole("USER"))
                .authorizeHttpRequests(req -> req.requestMatchers("/orders/listAll").hasRole("USER"))
                .authorizeHttpRequests(req -> req.requestMatchers("/orders/cancel/**").hasRole("USER"))
                .authorizeHttpRequests(req -> req.requestMatchers("/assets/**").hasRole("USER"))
                .authorizeHttpRequests(req -> req.requestMatchers("/login/**").hasRole("USER"))
                .authorizeHttpRequests(req -> req.requestMatchers("/orders/match").hasRole("ADMIN"))
                .authorizeHttpRequests(req -> req.requestMatchers("/h2-ui/**").hasRole("ADMIN"))
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userAdmin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .roles("USER","ADMIN")
                .build();

        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("fuat")
                .password("54321")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userAdmin, user1);
    }

}
