package com.sentinelscm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;

/**
 * Session-based form login shared by the Thymeleaf UI and the JSON API.
 * Fine-grained role checks live on the controllers via @PreAuthorize.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final RequestMatcher API_REQUESTS = request -> request.getRequestURI().startsWith("/api/");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/error", "/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")   // POST only while CSRF is enabled
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            // API clients get a bare 401; everyone else is sent to the login page
            .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint()));
        return http.build();
    }

    private static AuthenticationEntryPoint entryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> byRequest = new LinkedHashMap<>();
        byRequest.put(API_REQUESTS, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        DelegatingAuthenticationEntryPoint delegating = new DelegatingAuthenticationEntryPoint(byRequest);
        delegating.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
        return delegating;
    }
}
