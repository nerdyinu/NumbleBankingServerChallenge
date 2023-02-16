package com.example.numblebankingserverchallenge

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringExtension

import org.springframework.test.context.junit.jupiter.SpringExtension

class ProjectConfig:AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(
        SpringExtension
    )
}