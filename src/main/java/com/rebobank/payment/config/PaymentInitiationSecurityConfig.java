package com.rebobank.payment.config;

import com.rebobank.payment.exception.UnknownCertificateException;

import com.rebobank.payment.filter.CustomX509AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class PaymentInitiationSecurityConfig extends WebSecurityConfigurerAdapter
{
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        CustomX509AuthenticationFilter customFilter=new CustomX509AuthenticationFilter(http);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/initiate-payment").permitAll()
                .anyRequest().authenticated()
                .and()
                .x509()
                //.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .x509AuthenticationFilter(customFilter)
                //.userDetailsService(userDetailsService())
        ;

    }
    
    
    /*
     * @Bean
     * public UserDetailsService userDetailsService()
     * {
     * return (UserDetailsService) name ->
     * {
     * if (name.startsWith("Sandbox-TPP")) {
     * return new User(name, "",
     * AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
     * } else {
     * throw new UnknownCertificateException("Unknown Certificate");
     * }
     * };
     * }
     */

}