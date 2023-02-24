package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.RestDocsConfig
import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.service.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcBuilder
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "myapi.com")
@AutoConfigureMockMvc
@Import(RestDocsConfig::class)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
class MemberControllerIntegrationTest @Autowired constructor(

    private val memberService: MemberService,
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository
) {
    val signUpRequest = SignUpRequest("inu", "12345value")
    val friendSignup = SignUpRequest("friend1", "23456value")
    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val friend = Member(friendSignup.username, passwordEncoder.encode(friendSignup.pw))
    val returnMember: MemberDTO = MemberDTO(member)
    val loginRequest = LoginRequest(signUpRequest.username, "12345value")
    val mapper = jacksonObjectMapper()
    val session = MockHttpSession()
    val mySession = mapOf("user" to returnMember)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .alwaysDo<DefaultMockMvcBuilder>(
                document(
                    "{class-name}/{method-name}",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
                )
            )
            .build()
        memberRepository.save(member)
    }

    @AfterEach
    fun deleteAll() {
        memberRepository.deleteAll()
    }

    @Test
    fun `회원가입 실패-이미 존재하는 회원이름인 경우`() {
    }

    @Test
    fun `로그인 성공`() {
    }

    @Test
    fun `로그인 실패 - 비밀번호 오류`() {
    }

    @Test
    fun `친구 추가 성공 - 회원가입, 로그인 후 정상적으로`() {
    }

    @Test
    fun `친구 추가 실패 - 존재하지 않는 친구Id`() {
    }

    @Test
    @WithMockUser
    fun `존재하지 않는 친구id인 경우 404`() {
//        val friend = Member("friend1", passwordEncoder.encode("23456value"))
        val friendship = Friendship(member, friend)
        val randomId = UUID.randomUUID()
        mockMvc.post("/users/friends/${randomId}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isNotFound() }
        }.andDo {

            document(
                "{class-name}/{method-name}",
                responseFields(
                    fieldWithPath("timestamp").description("The timestamp of the error").type(JsonFieldType.STRING)
                        .optional(),
                    fieldWithPath("status").description("The HTTP status code of the error").type(JsonFieldType.NUMBER)
                        .optional(),
                    fieldWithPath("error").description("The HTTP error").type(JsonFieldType.STRING).optional(),
                    fieldWithPath("message").description("The error message").type(JsonFieldType.STRING).optional(),
                    fieldWithPath("path").description("The request path").type(JsonFieldType.STRING).optional()
                )

            )
        }
    }

    @Test
    @WithMockUser
    fun `자기 자신을 요청한 경우 400`() {
        val friendship = Friendship(member, friend)
        val randomId = UUID.randomUUID()
        mockMvc.post("/users/friends/${member.id}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isBadRequest() }
        }.andDo {

                document(
                    "{class-name}/{method-name}",
                    responseFields(
                        fieldWithPath("timestamp").description("The timestamp of the error").type(JsonFieldType.STRING)
                            .optional(),
                        fieldWithPath("status").description("The HTTP status code of the error")
                            .type(JsonFieldType.NUMBER).optional(),
                        fieldWithPath("error").description("The HTTP error").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("message").description("The error message").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("path").description("The request path").type(JsonFieldType.STRING).optional()
                    )
                )

        }
    }
//
//    @GetMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest, session: HttpSession): ResponseEntity<MemberDTO> {
//        val MemberDTO = memberService.login(loginRequest) ?: return ResponseEntity.badRequest().build()
//        session.setAttribute("user", MemberDTO)
//        return ResponseEntity.ok().body(MemberDTO)
//
//    }
//
//    @GetMapping("/users/{userId}/friends")
//    fun friendsList(@PathVariable("userId") userId: UUID): ResponseEntity<List<MemberDTO>> {
//        return memberService.getFriends(userId).let { ResponseEntity.ok().body(it) }
//    }
//
//    @PostMapping("/users/friends/{friendId}")
//    fun addFriend(@PathVariable("friendId") friendId: UUID, httpSession: HttpSession): ResponseEntity<FriendDTO> {
//        val user = httpSession.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//        val friendDTO=memberService.addFriend(user.id,friendId) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        return ResponseEntity.ok(friendDTO)
//    }
}