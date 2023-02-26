package com.example.numblebankingserverchallenge.controller


import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.service.MemberService
import com.example.numblebankingserverchallenge.util.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class, MockKExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc

class MemberControllerUnitTest @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    @MockkBean val memberService: MemberService,
    @MockkBean val memberRepository: MemberRepository
) {


    @Autowired
    lateinit var mockMvc: MockMvc


    @WithAnonymousUser
    @Test
    fun `signup - MemberDTO를 반환한다`() {

        every { memberService.createUser(signUpRequest = signUpRequest) } returns returnMember

        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(signUpRequest)
            accept = MediaType.APPLICATION_JSON

        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(mapper.writeValueAsString(returnMember)) }
        }
    }


    @Test

    fun `login - session에 MemberDTO가 추가된다`() {


        every { memberService.loadUserByUsername(loginRequest.username) } returns userdetails
        every { memberService.findByUsername(loginRequest.username) } returns returnMember

        val result = mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(loginRequest)
        }.andExpect { status { isOk() } }.andReturn()

        val session = result.request.session
        val user = session?.getAttribute("user") as? MemberDTO

        assertThat(user).isNotNull
        assertThat(user!!.username).isEqualTo(loginRequest.username)
    }

    @Test
    fun `login - 패스워드가 부정확한 경우 실패한다`() {
        val loginRequest = LoginRequest(signUpRequest.username, "12345value2")


        every { memberService.loadUserByUsername(loginRequest.username) } returns userdetails
        every { memberService.findByUsername(loginRequest.username) } returns returnMember
        val result = mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(loginRequest)
        }.andReturn()
        val session = result.request.session
        val member = session?.getAttribute("user") as? MemberDTO
        assertThat(member).isNull()
    }

    @Test
    @WithMockUser
    fun `getFriends- 인증되었다면 친구목록을 조회한다`() {
        val friend = Member("friend1", "23456value")
        val friendship = Friendship(member,friend)
        every { memberService.getFriends(returnMember.id) } returns listOf(FriendDTO(friendship))
        mockMvc.get("/users/friends") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(mapper.writeValueAsString(listOf(FriendDTO(friendship)))) }
        }
    }

    @Test
    @WithMockUser
    fun `친구목록 조회- 세션이 없는 경우 401에러`() {
        val friend = Member("friend1", "23456value")
        val friendship =Friendship(member,friend)
        every { memberService.getFriends(member.id) } returns listOf(FriendDTO(friendship))
        mockMvc.get("/users/friends") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser
    fun `인증된 경우 친구추가 성공`() {
        val friend = Member("friend1", "23456value")
        val friendship = Friendship(member, friend)

        every { memberService.addFriend(member.id, friend.id) } returns FriendDTO(friendship)
        mockMvc.post("/users/friends/${friend.id}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(mapper.writeValueAsString(FriendDTO(friendship))) }
        }
    }

    @Test
    @WithMockUser
    fun `세션 로그인 체크 실패 시- 401 UNAUTHORIZED`() {
        val friend = Member("friend1", "23456value")
        val friendship = Friendship(member, friend)
        every { memberService.addFriend(member.id, friend.id) } returns FriendDTO(friendship)
        mockMvc.post("/users/friends/${friend.id}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

}