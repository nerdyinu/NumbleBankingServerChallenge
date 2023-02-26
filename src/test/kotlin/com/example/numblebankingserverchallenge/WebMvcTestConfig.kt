package com.example.numblebankingserverchallenge

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@TestConfiguration
class WebMvcTestConfig {
    @Bean
    fun passwordEncoder():PasswordEncoder = BCryptPasswordEncoder()


}