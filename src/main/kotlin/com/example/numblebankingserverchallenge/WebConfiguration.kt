package com.example.numblebankingserverchallenge

import com.example.numblebankingserverchallenge.config.WebMvcConfig
import com.example.numblebankingserverchallenge.security.WebSecurityConfig
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableJpaAuditing
class WebConfiguration {
    @PersistenceContext
    lateinit var em:EntityManager

    @Bean
    fun jpaQueryFactory (): JPAQueryFactory = JPAQueryFactory(em)
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}