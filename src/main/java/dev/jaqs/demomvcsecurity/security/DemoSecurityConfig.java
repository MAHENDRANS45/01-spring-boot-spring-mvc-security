package dev.jaqs.demomvcsecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {

        UserDetails john = User.builder().
                username("john").
                password("{noop}test123").
                roles("EMPLOYEE").
                build();
        UserDetails Mary = User.builder().
                username("mary").
                password("{noop}test123").
                roles("EMPLOYEE", "MANAGER").
                build();
        UserDetails susan = User.builder().
                username("susan").
                password("{noop}test123").
                roles("EMPLOYEE", "MANAGER", "ADMIN").
                build();

        return new InMemoryUserDetailsManager(john, Mary, susan);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/css/**").permitAll()   // <-- Allow CSS files
                        // Restric access to different endpoints based on roles
                        .requestMatchers("/").hasRole("EMPLOYEE")
                        .requestMatchers("/leaders/**").hasRole("MANAGER")
                        .requestMatchers("/systems/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/showLoginPage")
                                .loginProcessingUrl("/authenticateTheUser") // No controller request mapping required ...
                                .permitAll()
                )
                // Log out handling
                .logout(logout -> logout.permitAll() // Adds logout support
                )
                // Custom Authentication Exception handling
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return http.build();
    }

}
