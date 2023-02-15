package com.example.numblebankingserverchallenge.repository.friendship

import com.example.numblebankingserverchallenge.domain.Member
import java.util.*

interface FriendshipRepositoryCustom {
    fun getFriends(id: UUID):List<Member>
}