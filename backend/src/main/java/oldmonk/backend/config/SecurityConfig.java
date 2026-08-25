package oldmonk.backend.config;

import com.google.api.client.http.HttpMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessHandler oauth2SuccessHandler;
    private final AuthenticationFailureHandler oauth2FailureHandler;

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
                )))
                .oauth2Login(
                        oauth2 -> oauth2.userInfoEndpoint(
                                userInfo -> userInfo
                                        .userService(githubOAuth2Service)
                        ).successHandler(oauth2SuccessHandler)
                                .failureHandler(oauth2FailureHandler)
                ).logout(
                        lg -> lg
                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(
                                        ((request, response, authentication) -> {
                                            response.setStatus(HttpStatus.NO_CONTENT.value());
                                        })
                                ).invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("OldMonk_SESSION")
                )
                .build();

    }

    @Bean
    AuthenticationSuccessHandler oauth2SuccessHandler(
            @Value("${app.frontend-url}") String frontendUrl
    ){
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(frontendUrl+"/auth/callback");
        return handler;
    }

    @Bean
    AuthenticationFailureHandler oauth2FailureHandler(
            @Value("${app.frontend-url}") String frontendUrl
    ){
        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
        handler.setDefaultFailureUrl(frontendUrl+"/login?error=oauth2_failed");
        return handler;
    }

}
