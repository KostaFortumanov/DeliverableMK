package com.dians.deliverable.auth_service.security;

import com.dians.deliverable.auth_service.models.UserRole;
import com.dians.deliverable.auth_service.security.jwt.AuthEntryPointJwt;
import com.dians.deliverable.auth_service.security.jwt.AuthTokenFilter;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final AuthEntryPointJwt unauthorizedHandler;

    private final static String[] publicMatchers = {
            "/auth/login",
            "/auth/newAccount",
            "/auth/register",
            "/ws/**"
    };

    private final static String[] managerMatchers = {
            "/api/drivers/allDriverInfo",
            "/api/drivers/delete/**",
            "/api/drivers/edit",
            "/api/drivers/selectDrivers",
            "/api/jobs/unassignedJobs",
            "/api/jobs/assignedJobs",
            "/api/jobs/completedJobs",
            "/api/jobs/addJob",
            "/api/jobs/assignJobs",
            "/api/jobs/preview",
            "/api/jobs/delete/**",
            "/api/locations/**",
            "/api/notifications/**"
    };

    private final static String[] driverMatchers = {
            "/api/map/allPaths",
            "/api/map/finishJob",
            "/api/map/currentJobs",
            "/api/jobs/myAssigned",
            "/api/jobs/myCompleted"
    };


    public SecurityConfig(UserService userService, AuthEntryPointJwt unauthorizedHandler) {
        this.userService = userService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(publicMatchers).permitAll()
                .antMatchers(managerMatchers).hasAuthority(UserRole.MANAGER.name())
                .antMatchers(driverMatchers).hasAuthority(UserRole.DRIVER.name())
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
