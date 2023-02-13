package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.UserDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface MemberRepository:JpaRepository<Member,UUID>, MemberRepositoryCustom,QuerydslPredicateExecutor<Member> {

    fun findByUsername(username:String):Member?
    fun findByUsernameAndEncryptedPassword(username: String, encrypted:String):Member?
}