package com.example.kfile.config;

import com.example.kfile.entity.Result;
import com.example.kfile.security.FileAuthorizationManager;
import com.example.kfile.security.JwtAuthenticationTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private FileAuthorizationManager fileAuthorizationManager;

    private ObjectMapper objectMapper;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void setJwtAuthenticationTokenFilter(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

    @Autowired
    public void setFileAuthorizationManager(FileAuthorizationManager fileAuthorizationManager) {
        this.fileAuthorizationManager = fileAuthorizationManager;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers((headers) ->
                        headers
                                .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                )
                .exceptionHandling(
                        (exceptionHandling) ->
                                exceptionHandling
                                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                                            PrintWriter responseWriter = response.getWriter();
                                            response.setContentType("application/json;charset=utf-8");
                                            responseWriter.write(objectMapper.writeValueAsString(Result.error()));
                                        })
                )
                .sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/user/login", "/user/register").permitAll()
                                .requestMatchers("/file/**").access(fileAuthorizationManager)
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
