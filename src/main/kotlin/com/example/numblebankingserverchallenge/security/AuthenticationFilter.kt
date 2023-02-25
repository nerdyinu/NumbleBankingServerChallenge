package com.example.numblebankingserverchallenge.security

import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.service.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class AuthenticationFilter (val memberService: MemberService): UsernamePasswordAuthenticationFilter() {
    val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        lateinit var token: UsernamePasswordAuthenticationToken
        try {
            val creds = mapper.readValue(request?.inputStream, LoginRequest::class.java)
            token = UsernamePasswordAuthenticationToken(creds.username, creds.pw, arrayListOf())
            logger.info("attempt authentication called!!")
        } catch (e: Exception) {

            throw RuntimeException(e)
        }
        return authenticationManager.authenticate(token)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {

        val user =(authResult?.principal as? User) ?: throw CustomException.BadRequestException()
        val userDetails =  memberService.findByUsername(user.username) ?: throw CustomException.UserNotFoundException()

        request?.session?.setAttribute("user", userDetails)

    }
}