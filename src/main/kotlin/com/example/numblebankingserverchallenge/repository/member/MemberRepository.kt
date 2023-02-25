package com.example.numblebankingserverchallenge.repository.member

import com.example.numblebankingserverchallenge.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository:JpaRepository<Member,UUID>, MemberRepositoryCustom{

    fun findByUsername(username:String):Member?
    fun findByUsernameAndEncryptedPassword(username: String, encrypted:String):Member?
}