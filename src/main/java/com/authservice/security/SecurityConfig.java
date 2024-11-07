package com.authservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()  // отключаем CSRF для простоты
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // используем сессионный режим без сохранения состояния
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()  // разрешаем доступ к endpoint'ам авторизации без токена
                .anyRequest().authenticated();  // защищаем все остальные endpoint'ы

        // Добавляем наш фильтр перед фильтром авторизации Spring
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
