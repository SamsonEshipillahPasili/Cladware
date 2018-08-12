package com.cladware.configuration;

import com.cladware.services.CladwareUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private CladwareUserDetailsService userDetailsService;

    /* Configure Authentication of Cladware users */
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    /* Configure Authorization of Cladware resources */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/css/*", "/js/*", "img/*", "/", "/products", "/register", "/logInPage")
                .permitAll()
                .antMatchers("/admin/addItem", "/admin/inventory", "/admin/manageOrders").hasAuthority("ADMIN")
                .antMatchers("/order", "/settings").hasAuthority("USER")
                .antMatchers("/dashboard").hasAnyAuthority("USER", "ADMIN")
                .and()
                .formLogin()
                .loginPage("/logInPage")
                .and()
                .logout()
                .logoutUrl("/logOut")
                .and()
                .csrf()
                .disable();
    }
}