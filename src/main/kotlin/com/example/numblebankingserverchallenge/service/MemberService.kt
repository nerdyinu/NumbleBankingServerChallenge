package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

import java.util.UUID



interface MemberService:UserDetailsService {
    fun createUser(signUpRequest: SignUpRequest):MemberDTO
    fun findByUsername(username:String):MemberDTO?
    fun getFriends(id:UUID):List<MemberDTO>
    fun addFriend(userId:UUID,friendId:UUID):FriendDTO
}