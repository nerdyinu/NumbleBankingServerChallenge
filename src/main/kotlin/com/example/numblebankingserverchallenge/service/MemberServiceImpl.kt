package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO

import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.SignUpRequest

import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.exception.UserExistsException
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository,
    private val passwordEncoder: PasswordEncoder
) :
    MemberService {
    override fun findByUsername(username: String): MemberDTO? =
        memberRepository.findByUsername(username)?.let(::MemberDTO)


    override fun createUser(signUpRequest: SignUpRequest): MemberDTO {
        memberRepository.findByUsername(signUpRequest.username)?.let{throw UserExistsException()}

        val encrypted = passwordEncoder.encode(signUpRequest.pw)
        val member = Member(signUpRequest.username, encrypted).let { memberRepository.save(it) }
        return MemberDTO(member)

    }

    override fun login(loginRequest: LoginRequest): MemberDTO? {

        val user = memberRepository.findByUsername(loginRequest.username) ?: return null

        return if (passwordEncoder.matches(loginRequest.pw, user.encryptedPassword)) MemberDTO(user) else null

    }

    override fun getFriends(id: UUID): List<MemberDTO> {
        val findMember = memberRepository.findById(id).orElseThrow { UserNotFoundException() }
        return friendshipRepository.getFriends(findMember.id).map(::MemberDTO)
    }
    @Transactional
    override fun addFriend(userId: UUID, friendId: UUID): FriendDTO {

        val findMember = memberRepository.findById(userId).orElse(null) ?: throw UserNotFoundException()
        val findFriend = memberRepository.findById(friendId).orElse(null) ?: throw UserNotFoundException()

        val friendShip = Friendship(findMember, findFriend)
        findMember.addFreind(friendShip)
        val friendShip2 = Friendship(findFriend, findMember)
        findFriend.addFreind(friendShip2)
        friendshipRepository.saveAll(listOf(friendShip2, friendShip)) // s-lock
        return FriendDTO(friendShip.id, findMember.username, findFriend.username)
    }
}