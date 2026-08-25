package oldmonk.backend.config;

import com.google.api.client.http.HttpMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                ).authorizeHttpRequests(
                        auth -> auth.requestMatchers(
                                "api/auth/login-url",
                                "oauth2/**",
                                "/login/oauth2",
                                "/error"
                        ).permitAll()
                                .requestMatchers(HttpMethods.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().permitAll()
                ).exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(
                        HttpStatus.UNAUTHORIZED
                ))).build();

    }
}
