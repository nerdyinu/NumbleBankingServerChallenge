package com.example.numblebankingserverchallenge.security

import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.service.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*


class AuthenticationFilter (val memberService: MemberService): UsernamePasswordAuthenticationFilter() {
    val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        lateinit var token: UsernamePasswordAuthenticationToken
        try {
            val creds = mapper.readValue(request?.inputStream, LoginRequest::class.java)
            token = UsernamePasswordAuthenticationToken(creds.username, creds.pw, arrayListOf())

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

        val user =(authResult?.principal as? User) ?: throw RuntimeException("")
        val userDetails =  memberService.findByUsername(user.username) ?: throw UserNotFoundException()
//        val key = env.getProperty("token.secret").let{
//            Decoders.BASE64.decode(it).let{ bytes -> Keys.hmacShaKeyFor(bytes)}
//        }
        request?.session?.setAttribute("user", userDetails)


//        val token=Jwts.builder()
//            .setSubject(userDetails.id.toString())
//            .setExpiration(Date(System.currentTimeMillis()+env.getProperty("token.expiration_time")!!.toLong()))
//            .signWith( key,SignatureAlgorithm.HS512)
//            .compact()
//        response?.addHeader("token",token)
//        response?.addHeader(    "userId",userDetails.id.toString())
    }
}