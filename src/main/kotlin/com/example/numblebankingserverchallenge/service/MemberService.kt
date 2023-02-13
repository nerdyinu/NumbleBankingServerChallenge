package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO
import com.example.numblebankingserverchallenge.dto.UserDTO

import org.springframework.security.core.userdetails.UserDetailsService
import java.util.UUID


interface MemberService {
    fun createUser(signUpVO: SignUpVO):UserDTO?
    fun findByUsername(username:String):UserDTO?
    fun login(loginVO: LoginVO):UserDTO?
    fun getFriends(id:UUID):List<UserDTO>?
    fun addFriend(userId:UUID,friendId:UUID):FriendDTO?
}