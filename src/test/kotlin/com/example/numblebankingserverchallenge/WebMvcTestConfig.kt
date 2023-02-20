package com.example.numblebankingserverchallenge

import com.example.numblebankingserverchallenge.config.WebMvcConfig
import com.example.numblebankingserverchallenge.security.WebSecurityConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@TestConfiguration
//@Import(WebSecurityConfig::class, WebMvcConfig::class)
class WebMvcTestConfig {
    @Bean
    fun passwordEncoder():PasswordEncoder = BCryptPasswordEncoder()


}