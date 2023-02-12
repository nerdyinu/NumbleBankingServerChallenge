package com.example.numblebankingserverchallenge

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@EnableJpaAuditing
@SpringBootApplication
class NumbleBankingServerChallengeApplication{
    @PersistenceContext
    lateinit var em: EntityManager
    @Bean
    fun jpaQueryFactory():JPAQueryFactory = JPAQueryFactory(em)
    @Bean
    fun passwordEncoder():PasswordEncoder =BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
    runApplication<NumbleBankingServerChallengeApplication>(*args)
}
