package com.example.kfile.config;

import com.example.kfile.security.FileAuthorizationManager;
import com.example.kfile.security.JwtAuthenticationTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private FileAuthorizationManager fileAuthorizationManager;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
                                .requestMatchers("/file/**").access(fileAuthorizationManager)
                )
                .formLogin((formLogin) ->
                        formLogin
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                                    PrintWriter responseWriter = httpServletResponse.getWriter();
                                    httpServletResponse.setContentType("application/json;charset=utf-8");
                                    responseWriter.write(objectMapper.writeValueAsString("登录成功"));
                                })
                                .failureHandler((httpServletRequest, httpServletResponse, e) -> {
                                    PrintWriter responseWriter = httpServletResponse.getWriter();
                                    responseWriter.write("login error!");
                                })
                                .permitAll()
                )
                .rememberMe(withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationTokenFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
