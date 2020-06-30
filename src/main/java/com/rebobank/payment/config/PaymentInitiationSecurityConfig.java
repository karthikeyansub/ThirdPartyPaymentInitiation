package com.rebobank.payment.config;

import com.rebobank.payment.exception.UnknownCertificateException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class PaymentInitiationSecurityConfig extends WebSecurityConfigurerAdapter
{
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.cors().and().csrf().disable().authorizeRequests()
             .antMatchers(HttpMethod.POST, "/initiate-payment").permitAll()
             .anyRequest().authenticated()
             .and().x509()
             .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
             .userDetailsService(userDetailsService());
    }
    
    
    @Bean
    public UserDetailsService userDetailsService()
    {
        return (UserDetailsService) name ->
        {
            if (name.startsWith("Sandbox-TPP")) {
                return new User(name, "",
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
            } else {
                throw new UnknownCertificateException("Unknown Certificate");
            }
        };
    }

}