package com.example.numblebankingserverchallenge.repository.friendship

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO
import java.util.*

interface FriendshipRepositoryCustom {
    fun getFriends(id: UUID):List<Friendship>
    fun findFriend(memberId:UUID,friendID:UUID):Friendship?
}