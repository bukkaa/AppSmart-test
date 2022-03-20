package com.bukkaa.appsmart.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter tokenFilter;
    private final AuthEntryPoint authenticationEntryPoint;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable().csrf().disable().httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/v1/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                    .antMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                    .antMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                    .anyRequest().permitAll();

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }


}
