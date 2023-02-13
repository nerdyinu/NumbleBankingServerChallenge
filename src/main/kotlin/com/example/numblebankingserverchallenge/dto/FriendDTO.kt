package com.example.numblebankingserverchallenge.dto

import com.example.numblebankingserverchallenge.domain.Friendship
import java.util.*

data class FriendDTO(val id: UUID, val username:String, val friendName:String){
    constructor(friendship: Friendship):this(friendship.id,friendship.user.username, friendship.friend.username)
}
