package com.rabobank.payment.config;

import com.rabobank.payment.filter.CustomX509AuthenticationFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class PaymentInitiationSecurityConfig extends WebSecurityConfigurerAdapter
{

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        CustomX509AuthenticationFilter customFilter = new CustomX509AuthenticationFilter(http);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
                .disable().exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()).and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/initiate-payment").permitAll()
                .anyRequest().authenticated().and().x509().x509AuthenticationFilter(customFilter);

    }

}