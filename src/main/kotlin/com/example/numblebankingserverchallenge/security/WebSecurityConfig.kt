package com.example.numblebankingserverchallenge.security

import com.example.numblebankingserverchallenge.service.MemberService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.IpAddressMatcher
import java.util.function.Supplier


@EnableWebSecurity
@Configuration
class WebSecurityConfig @Autowired constructor(val memberService: MemberService) {
    @Bean
    fun securityFilterChain(http: HttpSecurity,authConfig: AuthenticationConfiguration): SecurityFilterChain {

       http.csrf()
            .disable()
            .authorizeHttpRequests().requestMatchers("/signup", "/login").permitAll()
            .anyRequest().authenticated()
            .and()
           .userDetailsService(memberService)
           .addFilterAt(authenticationFilter(authConfig.authenticationManager), UsernamePasswordAuthenticationFilter::class.java)
            .headers().frameOptions().disable()
        return http.build()

    }


//    @Bean
    fun authenticationFilter(authManager:AuthenticationManager):UsernamePasswordAuthenticationFilter {
        return AuthenticationFilter(memberService).also { it.setAuthenticationManager(authManager) }
    }


    private fun hasIpAddress(ipAddress: String): AuthorizationManager<RequestAuthorizationContext>? {
        val ipAddressMatcher = IpAddressMatcher(ipAddress)
        return AuthorizationManager { authentication: Supplier<Authentication?>?, context: RequestAuthorizationContext ->
            val request: HttpServletRequest = context.request
            AuthorizationDecision(ipAddressMatcher.matches(request))
        }
    }



}