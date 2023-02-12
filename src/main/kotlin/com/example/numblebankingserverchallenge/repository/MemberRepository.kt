package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberRepository:JpaRepository<Member,UUID> {
}