package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.NumbleBankingServerChallengeApplication
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest(classes = [NumbleBankingServerChallengeApplication::class])
@ActiveProfiles("test")
@Transactional

@ExtendWith(SpringExtension::class)
class MemberServiceIntegrationTest @Autowired constructor(
    private val friendshipRepository: FriendshipRepository,
    private val passwordEncoder: PasswordEncoder,
    private val memberService: MemberService,
    private val memberRepository: MemberRepository
) {
    val username = "Inu"
    val pw1 = "12345"
    val signUpRequest = SignUpRequest(username, pw1)

    @BeforeEach
    fun init() {
        memberRepository.deleteAll()
        friendshipRepository.deleteAll()
    }

    @Test
    fun `회원가입 성공`() {
        val createdUser = memberService.createUser(signUpRequest)
        assertThat(createdUser).isNotNull
        assertThat(createdUser?.username).isEqualTo(signUpRequest.username)
        val savedUser = memberRepository.findByUsername(signUpRequest.username)
        assertThat(passwordEncoder.matches(signUpRequest.pw, savedUser?.encryptedPassword)).isTrue

    }

    @Test
    fun `이미 존재하는 유저이름으로 회원가입 실패`() {


        val createdUser = memberService.createUser(signUpRequest)
        assertThat(createdUser).isNotNull
        assertThat(createdUser?.username).isEqualTo(signUpRequest.username)
        val signUpRequest2 = SignUpRequest(username, "23456")

        assertThrows<CustomException.UserNotFoundException> { memberService.createUser(signUpRequest2) }

    }

    @Test
    fun `로그인 성공`() {
        memberService.createUser(signUpRequest)
        val loginRequest = LoginRequest(username, pw1)
//        val loggedInUser = memberService.login(loginRequest)
//        assertThat(loggedInUser).isNotNull
//        assertThat(loggedInUser?.username).isEqualTo(signUpRequest.username)
    }

    @Test
    fun `로그인 실패`() {
        memberService.createUser(signUpRequest)
        val loginRequest = LoginRequest(username, "23456")
//        val failedLoginUser = memberService.login(loginRequest)
//        assertThat(failedLoginUser).isNull()
    }

    @Test
    fun `친구추가 성공`() {
        val user = Member(username, passwordEncoder.encode(pw1))
        val friend = Member("friend1", passwordEncoder.encode(pw1))
        memberRepository.saveAll(listOf(user, friend))

        val saveduser = memberRepository.findByUsername(username)
        val savedFriend = memberRepository.findByUsername("friend1")
        assertThat(saveduser).isNotNull
        assertThat(savedFriend).isNotNull
        val friendShipDTO = memberService.addFriend(saveduser!!.id, savedFriend!!.id)
        //user가 없거나 friend가 없는 경우 null 반환
        //user-> friend, friend->user, 2개 friendship

        val 한방향관계 = memberService.getFriends(saveduser.id)
        val 양방향관계 = memberService.getFriends(savedFriend.id)

        assertThat(friendShipDTO).isNotNull
        assertThat(한방향관계?.size).isEqualTo(1)
        assertThat(한방향관계?.get(0)?.username).isEqualTo(savedFriend.username)
        assertThat(양방향관계?.size).isEqualTo(1)
        assertThat(양방향관계?.get(0)?.username).isEqualTo(saveduser.username)
    }

    @Test
    fun `친구추가 실패`() {
        val user = Member(username, passwordEncoder.encode(pw1))
        val friend = Member("friend1", passwordEncoder.encode(pw1))

        memberRepository.saveAll(listOf(user, friend))

        val saveduser = memberRepository.findByUsername(username)
        val savedFriend = memberRepository.findByUsername("friend2")
        assertThat(saveduser).isNotNull
        assertThat(savedFriend).isNull()
        assertThrows<CustomException.UserNotFoundException> { memberService.addFriend(saveduser!!.id, UUID.randomUUID()) }

    }

    @Test
    fun `친구목록 조회`() {
        val user = Member(username, passwordEncoder.encode(pw1))
        val friend = Member("friend1", passwordEncoder.encode(pw1))
        memberRepository.saveAll(listOf(user, friend))

        val saveduser = memberRepository.findByUsername(username)
        val savedFriend = memberRepository.findByUsername("friend1")
        assertThat(saveduser).isNotNull
        assertThat(savedFriend).isNotNull
        val friendShipDTO = memberService.addFriend(saveduser!!.id, savedFriend!!.id)

        val friends = memberService.getFriends(saveduser.id)
        assertThat(friends).hasSize(1)
        assertThat(friends?.get(0)?.id).isEqualTo(savedFriend.id)
        val opposite = memberService.getFriends(savedFriend.id)

        assertThat(opposite).hasSize(1)
        assertThat(opposite?.get(0)?.id).isEqualTo(user.id)
    }

}