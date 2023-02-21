package com.example.numblebankingserverchallenge

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceContext
import jakarta.persistence.PersistenceUnit
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@TestConfiguration
class JpaTestConfig{
    @PersistenceUnit
    lateinit var emf:EntityManagerFactory
    @PersistenceContext
    lateinit var em:EntityManager
    @Bean
    fun testEntityManager():TestEntityManager = TestEntityManager(emf)
    @Bean
    fun jpaQueryFactory():JPAQueryFactory = JPAQueryFactory(em)
    @Bean
    fun passwordEncoder():PasswordEncoder = BCryptPasswordEncoder()
}

