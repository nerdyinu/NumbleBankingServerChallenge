package com.example.numblebankingserverchallenge.dto

import com.example.numblebankingserverchallenge.domain.Member
import com.querydsl.core.annotations.QueryProjection
import java.io.Serializable
import java.util.*

data class MemberDTO @QueryProjection constructor (val id: UUID, val username:String):Serializable{
    constructor(member: Member): this(member.id, member.username)
}