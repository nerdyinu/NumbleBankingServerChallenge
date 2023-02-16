package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.service.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.servlet.http.HttpSession
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID

@ExtendWith(SpringExtension::class, MockKExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MemberControllerUnitTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val passwordEncoder: PasswordEncoder,
    @MockkBean private val memberService: MemberService,
    @MockkBean private val memberRepository: MemberRepository
) {
    val signUpRequest = SignUpRequest("inu", "12345value")
    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val returnMember: MemberDTO = MemberDTO(member)
    val loginRequest = LoginRequest(signUpRequest.username, "12345value")
    val mapper = ObjectMapper()
    val session = MockHttpSession()
    val mySession= mapOf("user" to returnMember)
    @AfterEach
    fun clean(){
        session.clearAttributes()
    }
    @Test

    fun `signup - MemberDTO를 반환한다`() {

        every { memberService.createUser(signUpRequest) } returns returnMember

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

        val userdetails = User(member.username, member.encryptedPassword, arrayListOf())
        every { memberService.loadUserByUsername(loginRequest.username) } returns userdetails
        every {memberService.findByUsername(loginRequest.username)} returns returnMember
        val result=mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(loginRequest)
        }.andExpect { status { isOk() } }.andReturn()
        val session = result.request.session
        val member = session?.getAttribute("user") as? MemberDTO
        assertThat(member).isNotNull
        assertThat(member?.username).isEqualTo(loginRequest.username)
    }
    @Test
    fun `login - 패스워드가 부정확한 경우 실패한다`(){
        val loginRequest = LoginRequest(signUpRequest.username, "12345value2")

        val userdetails = User(loginRequest.username, member.encryptedPassword, arrayListOf())
        every { memberService.loadUserByUsername(loginRequest.username) } returns userdetails
        every {memberService.findByUsername(loginRequest.username)} returns returnMember
        val result=mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(loginRequest)
        }.andReturn()
        val session = result.request.session
        val member = session?.getAttribute("user") as? MemberDTO
        assertThat(member).isNull()
    }

    @Test
    @WithMockUser(username = "inu")
    fun `getFriends- 인증되었다면 친구목록을 조회한다`(){
        val friend = Member("friend1", passwordEncoder.encode("23456value"))
        every { memberService.getFriends(returnMember.id) } returns listOf(MemberDTO(friend))
        mockMvc.get("/users/friends"){
            contentType = MediaType.APPLICATION_JSON
            accept=  MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(mapper.writeValueAsString(listOf(MemberDTO(friend)))) }
        }
    }
    @Test
    @WithMockUser
    fun `친구목록 조회- 세션이 없는 경우 401에러`(){
        val friend = Member("friend1", passwordEncoder.encode("23456value"))
        every { memberService.getFriends(member.id) } returns listOf(MemberDTO(friend))
        mockMvc.get("/users/friends"){
            contentType = MediaType.APPLICATION_JSON
            accept=  MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized()}
        }
    }
    @Test
    @WithMockUser
    fun `인증된 경우 친구추가 성공`(){
        val friend = Member("friend1", passwordEncoder.encode("23456value"))
        val friendship = Friendship(member, friend)
        every { memberService.addFriend(member.id, friend.id) } returns FriendDTO(friendship)
        mockMvc.post("/users/friends/${friend.id}"){
            contentType = MediaType.APPLICATION_JSON
            accept=  MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk()}
            content{ contentType(MediaType.APPLICATION_JSON)}
            content { json(mapper.writeValueAsString(FriendDTO(friendship))) }
        }
    }
    @Test
    @WithMockUser
    fun `세션에 정보가 없는 경우 401 UNAUTHORIZED`(){
        val friend = Member("friend1", passwordEncoder.encode("23456value"))
        val friendship = Friendship(member, friend)
        every { memberService.addFriend(member.id, friend.id) } returns FriendDTO(friendship)
        mockMvc.post("/users/friends/${friend.id}"){
            contentType = MediaType.APPLICATION_JSON
            accept=  MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized()}
        }
    }

//    @GetMapping("/users/friends")
//    fun friendsList(session: HttpSession): ResponseEntity<List<MemberDTO>> {
//        val member = session.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//        return memberService.getFriends(member.id).let { ResponseEntity.ok().body(it) }
//    }
//
//    @PostMapping("/users/friends/{friendId}")
//    fun addFriend(@PathVariable("friendId") friendId:UUID, httpSession: HttpSession): ResponseEntity<FriendDTO> {
//        val user = httpSession.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//        try{
//            return memberService.addFriend(user.id,friendId).let { ResponseEntity.ok(it) }
//        }catch (ex: UserNotFoundException){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        }
//    }
}