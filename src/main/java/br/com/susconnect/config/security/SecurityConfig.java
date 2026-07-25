package br.com.susconnect.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação.
 *
 * Durante o desenvolvimento do MVP,
 * todas as rotas permanecerão liberadas.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(

                                "/swagger-ui/**",
                                "/v3/api-docs/**"

                        ).permitAll()

                        .anyRequest()

                        .permitAll())

                .httpBasic(Customizer.withDefaults());

        return http.build();

    }

}