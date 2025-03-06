package org.oauth2client120250105.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/",
                                "/success-logout",
                                "/without-consent-logout",
                                "/check-consent").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                /*.logout((logout) -> (logout)
                        .logoutUrl("http://authserver:9000/oauth2/logout")
                        //.logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))*/
                .oauth2Client(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
