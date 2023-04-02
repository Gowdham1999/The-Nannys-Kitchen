package com.cma.main.JWT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    CustomerDetailsService customerDetailsService;
    @Autowired
    JwtFilter jwtfilter;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customerDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/users/login", "/users/signUp", "/users/forgotPassword", "/swagger-ui/**", "/v3/**", "/js/**", "/css/**")//These URLS will not be validated and will be permitted without any check.
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        The filterChain method takes an HttpSecurity object as a parameter, which is used to configure the security filter chain. The configuration in this method includes:
//
//        cors(): Enables Cross-Origin Resource Sharing (CORS) support for the application, allowing resources to be requested from different domains.
//
//        csrf().disable(): Disables Cross-Site Request Forgery (CSRF) protection, which is a security feature that prevents unauthorized requests from being sent from a user's browser.
//
//        authorizeHttpRequests(): Begins the configuration of authorization rules for incoming HTTP requests.
//
//        requestMatchers("/users/login", "/users/signUp", "/users/forgotPassword"): Defines a list of URL patterns that should be exempt from authorization checks and allowed to be accessed without authentication.
//
//        permitAll(): Grants access to the exempt URL patterns to all users, regardless of whether they are authenticated or not.
//
//        anyRequest().authenticated(): Specifies that any incoming request that does not match the exempt URL patterns should require authentication to be accessed.
//
//        exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS): Configures the handling of exceptions thrown during authentication and sets the session creation policy to stateless, which means that the application does not create or store a session for authenticated users.
//
//        Overall, this configuration sets up a basic security filter chain for a Spring application with CORS and CSRF disabled, and allows unauthenticated access to certain URLs while requiring authentication for all other requests.

        http.addFilterBefore(jwtfilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
