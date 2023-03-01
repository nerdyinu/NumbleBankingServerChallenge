package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemberServiceImpl (

    private val friendshipRepository: FriendshipRepository,
    private val passwordEncoder: PasswordEncoder
) : MemberService {
    @Autowired
    lateinit var memberRepository:MemberRepository
    val logger = LoggerFactory.getLogger(this::class.java)
    override fun findByUsername(username: String): MemberDTO? =
        memberRepository.findByUsername(username)?.let(::MemberDTO)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("loadUserByUsername called!!")
        val member = username.let{memberRepository.findByUsername(it) ?: throw CustomException.UserNotFoundException() }
        return User(member.username, member.encryptedPassword, mutableListOf())
    }

    override fun createUser(signUpRequest: SignUpRequest): MemberDTO {
        memberRepository.findByUsername(signUpRequest.username)?.let{throw CustomException.UserExistsException()}
        val encrypted = passwordEncoder.encode(signUpRequest.pw)
        val member = Member(signUpRequest.username, encrypted).let { memberRepository.save(it) }
        return MemberDTO(member)
    }


    override fun getFriends(id: UUID): List<FriendDTO> {
        val findMember = memberRepository.findById(id).orElseThrow { CustomException.UserNotFoundException() }
        return friendshipRepository.getFriends(findMember.id).map(::FriendDTO)
    }
    @Transactional
    override fun addFriend(userId: UUID, friendId: UUID): FriendDTO {

        val findMember = memberRepository.findById(userId).orElse(null) ?: throw CustomException.UserNotFoundException()
        val findFriend = memberRepository.findById(friendId).orElse(null) ?: throw CustomException.UserNotFoundException()
        if(userId==friendId) throw CustomException.BadRequestException()
        val friendShip = Friendship(findMember, findFriend)
        val friendShip2 = Friendship(findFriend, findMember)
        val (f1,f2)=friendshipRepository.saveAll(listOf(friendShip, friendShip2)) // s-lock
        return FriendDTO(f1)
    }
}