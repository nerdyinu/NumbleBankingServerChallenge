package com.example.numblebankingserverchallenge.config

import com.example.numblebankingserverchallenge.dto.MemberDTO
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Component
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SessionLoginChecker
