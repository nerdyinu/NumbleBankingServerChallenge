package com.example.numblebankingserverchallenge.repository.member

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.domain.QFriendship.*
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QMember.*

import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.*

class MemberRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): MemberRepositoryCustom {

}