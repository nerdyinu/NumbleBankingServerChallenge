package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO

import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO

import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.exception.UserExistsException
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
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


    override fun createUser(signUpVO: SignUpVO): MemberDTO {
        memberRepository.findByUsername(signUpVO.username) ?: throw UserExistsException()

        val encrypted = passwordEncoder.encode(signUpVO.pw)
        val member = Member(signUpVO.username, encrypted).let { memberRepository.save(it) }
        return MemberDTO(member)

    }

    override fun login(loginVO: LoginVO): MemberDTO? {

        val user = memberRepository.findByUsername(loginVO.username) ?: return null

        return if (passwordEncoder.matches(loginVO.pw, user.encryptedPassword)) MemberDTO(user) else null

    }

    override fun getFriends(id: UUID): List<MemberDTO> {
        val findMember = memberRepository.findById(id).orElseThrow { UserNotFoundException() }
        return friendshipRepository.getFriends(findMember.id).map(::MemberDTO)
    }

    override fun addFriend(userId: UUID, friendId: UUID): FriendDTO {

        val findMember = memberRepository.findById(userId).orElse(null) ?: throw UserNotFoundException()
        val findFriend = memberRepository.findById(friendId).orElse(null) ?: throw UserNotFoundException()

        val friendShip = Friendship(findMember, findFriend)
        val friendShip2 = Friendship(findFriend, findMember)
        friendshipRepository.saveAll(listOf(friendShip2, friendShip))
        return FriendDTO(friendShip.id, findMember.username, findFriend.username)
    }
}