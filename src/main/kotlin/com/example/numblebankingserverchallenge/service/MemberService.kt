package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO
import com.example.numblebankingserverchallenge.dto.MemberDTO

import org.springframework.security.core.userdetails.UserDetailsService
import java.util.UUID


interface MemberService {
    fun createUser(signUpVO: SignUpVO):MemberDTO
    fun findByUsername(username:String):MemberDTO?
    fun login(loginVO: LoginVO):MemberDTO?
    fun getFriends(id:UUID):List<MemberDTO>
    fun addFriend(userId:UUID,friendId:UUID):FriendDTO
}