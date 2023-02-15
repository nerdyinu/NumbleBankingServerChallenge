package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
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
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
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
    @MockkBean private val memberService: MemberService
) {
    val signUpRequest = SignUpRequest("inu", "12345value")
    val returnMember: MemberDTO = MemberDTO(UUID.randomUUID(), signUpRequest.username)
    val loginRequest = LoginRequest("inu", "12345value")
    val mapper = ObjectMapper()
    val session = MockHttpSession()

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
    fun `login - session에 MemberDTO객체 추가 성공`() {
        val encoded = passwordEncoder.encode(loginRequest.pw)
        every { memberService.loadUserByUsername(loginRequest.username) } returns User(
            loginRequest.username, encoded,
            mutableListOf()
        )
        every {memberService.findByUsername(loginRequest.username)} returns MemberDTO(UUID.randomUUID(), loginRequest.username)
        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(loginRequest)
            accept = MediaType.APPLICATION_JSON
            sessionAttrs
        }
        val member = session.getAttribute("user") as MemberDTO
        assertThat(member).isNotNull
        assertThat(member.username).isEqualTo(loginRequest.username)
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