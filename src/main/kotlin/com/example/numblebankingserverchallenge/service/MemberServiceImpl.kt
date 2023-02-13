package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO

import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO

import com.example.numblebankingserverchallenge.dto.UserDTO
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberServiceImpl(private val memberRepository: MemberRepository, private val friendshipRepository: FriendshipRepository, private val passwordEncoder: PasswordEncoder) :
    MemberService {
    override fun findByUsername(username: String): UserDTO? =
        memberRepository.findByUsername(username)?.let { UserDTO(it.id, it.username) }


    override fun createUser(signUpVO: SignUpVO): UserDTO? {
        return memberRepository.findByUsername(signUpVO.username)?.let { return null } ?: run {
            val encrypted = passwordEncoder.encode(signUpVO.pw)
            val member = Member(signUpVO.username, encrypted).let { memberRepository.save(it) }
            return UserDTO(member.id, member.username)
        }
    }

    override fun login(loginVO: LoginVO): UserDTO? {

        return memberRepository.findByUsername(loginVO.username)?.let {
            if(passwordEncoder.matches(loginVO.pw, it.encryptedPassword)) UserDTO(it.id,it.username)
            else null
        }
    }

    override fun getFriends(id: UUID): List<UserDTO>? {
        val findMember = memberRepository.findById(id).orElseGet { null }?: return null
        return friendshipRepository.getFriends(findMember.id).map { UserDTO(it.id,it.username) }
    }

    override fun addFriend(userId:UUID,friendId: UUID): FriendDTO? {

        val findMember = memberRepository.findById(userId).orElse(null) ?:return null
        val findFriend = memberRepository.findById(friendId).orElse(null)?: return null

        val friendShip =Friendship(findMember, findFriend)
        val friendShip2 = Friendship(findFriend,findMember)
         friendshipRepository.saveAll(listOf(friendShip2,friendShip))
        return FriendDTO(friendShip.id,findMember.username,findFriend.username)
    }
}