package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.UserDTO
import java.util.UUID

interface MemberRepositoryCustom {
    fun getFriends(id:UUID):List<Member>
}