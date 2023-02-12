package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QMember.*
import com.querydsl.jpa.impl.JPAQueryFactory

class MemberRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):MemberRepositoryCustom {
    fun findFriends(owner:Member):List<Member>{
        val owner = jpaQueryFactory.select(member).from(member).fetchJoin().where( member.username.eq(owner.username)).fetchFirst()
        val res= mutableListOf<Member>()
        owner.friends.forEach {
            jpaQueryFactory.selectFrom(member).where(member.id.eq(it.receiver.id)) .fetchFirst().let { res.add(it) }
        }
        return res.toList()
    }
}