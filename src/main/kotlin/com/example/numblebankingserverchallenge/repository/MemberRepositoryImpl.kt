package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.domain.QFriendship
import com.example.numblebankingserverchallenge.domain.QFriendship.*
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QMember.*

import com.example.numblebankingserverchallenge.dto.QUserDTO
import com.example.numblebankingserverchallenge.dto.UserDTO
import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.*

class MemberRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):MemberRepositoryCustom {
    override fun getFriends(id: UUID): List<Member> {
        val member2 = QMember("member2")
        return jpaQueryFactory.select(friendship.friend).from(friendship).where(friendship.user.id.eq(id)).leftJoin(
            friendship.friend, member).leftJoin(friendship.user,member2).fetchJoin().fetch()
    }
}