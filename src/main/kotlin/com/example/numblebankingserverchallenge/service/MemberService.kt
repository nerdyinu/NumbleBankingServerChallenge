package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*


interface MemberService:UserDetailsService {
    fun createUser(signUpRequest: SignUpRequest):MemberDTO
    fun findByUsername(username:String):MemberDTO?
    fun getFriends(id:UUID):List<FriendDTO>
    fun addFriend(userId:UUID,friendId:UUID):FriendDTO
}