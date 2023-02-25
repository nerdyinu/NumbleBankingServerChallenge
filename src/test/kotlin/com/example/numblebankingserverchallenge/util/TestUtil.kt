package com.example.numblebankingserverchallenge.util

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


val passwordEncoder:PasswordEncoder  =BCryptPasswordEncoder()
    val signUpRequest = SignUpRequest("inu", "12345value")
    val friendSignup = SignUpRequest("friend1", "23456value")

    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val friend = Member(friendSignup.username, passwordEncoder.encode(friendSignup.pw))
    val returnMember: MemberDTO = MemberDTO(member)
    val loginRequest = LoginRequest(signUpRequest.username, signUpRequest.pw)
    val mapper = jacksonObjectMapper()
    val session = MockHttpSession()
    val mySession = mapOf("user" to returnMember)
    val account = Account(member, "account1", AccountBalance(3000L))
    val friendAccount = Account(friend, "ac2", AccountBalance(3000L))
    val userdetails = User(member.username, member.encryptedPassword, arrayListOf())
    fun myIdentifier(methodName: String) = "{class-name}/$methodName"
    val returnAccount = AccountDTO(account)
