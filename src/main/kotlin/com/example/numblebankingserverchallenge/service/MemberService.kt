package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO

import java.util.UUID


interface MemberService {
    fun createUser(signUpRequest: SignUpRequest):MemberDTO
    fun findByUsername(username:String):MemberDTO?
    fun login(loginRequest: LoginRequest):MemberDTO?
    fun getFriends(id:UUID):List<MemberDTO>
    fun addFriend(userId:UUID,friendId:UUID):FriendDTO
}