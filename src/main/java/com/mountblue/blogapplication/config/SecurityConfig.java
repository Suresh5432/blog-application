package com.mountblue.blogapplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(auth->auth
                                .requestMatchers(
                                        "/",
                                        "/register",
                                        "/login",
                                        "/css/**",
                                        "/posts"
                                    ).permitAll()

                                .requestMatchers(HttpMethod.GET, "/posts/**")
                                    .permitAll()

                                .requestMatchers(HttpMethod.POST, "/posts/*/comments")
                                    .permitAll()

                                .requestMatchers("/api/auth/login")
                                    .permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/**")
                                        .permitAll()

                                .requestMatchers(HttpMethod.POST, "/api/posts")
                                        .authenticated()

                                .requestMatchers(HttpMethod.PUT, "/api/posts/**")
                                        .authenticated()

                                .requestMatchers(HttpMethod.DELETE, "/api/posts/**")
                                        .authenticated()
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/comments/**"
                                ).permitAll()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/posts/*/comments"
                                ).authenticated()

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/comments/**"
                                ).authenticated()

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/comments/**"
                                ).authenticated()

                                .anyRequest().authenticated()
                                    )

                .formLogin(form-> form

                                .loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/posts", true)
                                .failureUrl("/login?error=true")
                                .permitAll()
                )

                .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )
                .exceptionHandling(exe->exe
                                        .defaultAuthenticationEntryPointFor(
                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                request -> request.getRequestURI()
                                                        .startsWith("/api")
                                        )
                );

        return http.build();
    }

}
