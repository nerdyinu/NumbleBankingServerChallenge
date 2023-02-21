package com.example.numblebankingserverchallenge.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig:WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loggedInMemberResolver())
    }
    @Bean
    fun loggedInMemberResolver():HandlerMethodArgumentResolver = SessionHandlerMethodArgumentResolver()
}