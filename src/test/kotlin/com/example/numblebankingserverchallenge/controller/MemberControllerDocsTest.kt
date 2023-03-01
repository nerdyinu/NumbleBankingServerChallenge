package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.*
import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.*
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(RestDocsConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@Transactional
class MemberControllerDocsTest @Autowired constructor(
    private val friendshipRepository: FriendshipRepository,
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository
) {


    @Autowired
    lateinit var mockMvc: MockMvc
    lateinit var user: Member
    lateinit var friend1: Member
    val signUpRequest = SignUpRequest("inu", "12345value")
    val friendSignup = SignUpRequest("friend1", "23456value")
    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val friend = Member(friendSignup.username, passwordEncoder.encode(friendSignup.pw))
    val returnMember: MemberDTO = MemberDTO(member)
    val loginRequest = LoginRequest(signUpRequest.username, signUpRequest.pw)
    val mapper = jacksonObjectMapper()
    val mySession = mapOf("user" to returnMember)

    fun myIdentifier(methodName: String) = "{class-name}/$methodName"

    @BeforeEach
    fun setUp() {

        user = memberRepository.save(member)
        friend1 = memberRepository.save(friend)
    }

    @AfterEach
    fun deleteAll() {
        memberRepository.deleteAll()
        friendshipRepository.deleteAll()
    }

    @Test
    fun `회원가입 실패-이미 존재하는 회원이름인 경우`() {

        val newRequest = SignUpRequest("inu2", "12345")
        val newm = Member(newRequest.username, passwordEncoder.encode(newRequest.pw))
        mockMvc.post("/signup") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(signUpRequest)
            accept = APPLICATION_JSON

        }.andExpect {
            status { isConflict() }
            content { contentType(APPLICATION_JSON) }
        }

    }

    @Test
    fun `회원가입 성공`() {

        val newRequest = SignUpRequest("inu2", "12345")
        val newm = Member(newRequest.username, passwordEncoder.encode(newRequest.pw))
        mockMvc.post("/signup") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(newRequest)
            accept = APPLICATION_JSON

        }.andExpect {
            status { isOk() }
            content { contentType(APPLICATION_JSON) }
            content { jsonPath("$.username") { value(newRequest.username) } }
        }.andDo {
            handle(
                document(
                    myIdentifier("회원가입"),
                    requestFields(
                        fieldWithPath("username").description("signup username").type(STRING),
                        fieldWithPath("pw").description("signup password").type(STRING)
                    ),
                    responseFields(
                        fieldWithPath("username").description("created user's username").type(STRING),
                        fieldWithPath("id").description("created user's id").type(STRING),
                    )
                )
            )
        }
    }

    /*
    * 로그인은 spring security의
    * UsernamePasswordAuthenticationFilter의 디폴트 엔드포인트를 사용
    * */
    @Test
    fun `로그인 성공`() {


        val result = mockMvc.post("/login") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(LoginRequest(user.username, signUpRequest.pw))

        }.andExpect {
            status { isOk() }

        }.andDo {
            handle(
                document(
                    myIdentifier("로그인"),
                    requestFields(
                        fieldWithPath("username").description("login username").type(STRING),
                        fieldWithPath("pw").description("login password").type(STRING)
                    )
                )
            )
        }.andReturn()

        val session = result.request.session
        val user = session?.getAttribute("user") as? MemberDTO

        Assertions.assertThat(user).isNotNull
        Assertions.assertThat(user!!.username).isEqualTo(loginRequest.username)
    }

    @Test
    fun `로그인 실패 - 비밀번호 오류`() {
        mockMvc.post("/login") {
            contentType = APPLICATION_JSON
            characterEncoding = "utf-8"
            content = mapper.writeValueAsString(LoginRequest(signUpRequest.username, friendSignup.pw))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    //    @PostMapping("/users/friends/{friendId}")
//    fun addFriend(@PathVariable("friendId") friendId: UUID, httpSession: HttpSession): ResponseEntity<FriendDTO> {
//        val user = httpSession.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//        val friendDTO=memberService.addFriend(user.id,friendId) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        return ResponseEntity.ok(friendDTO)
//    }
    @Test
    @WithMockUser
    fun `존재하지 않는 친구id인 경우 404`() {

        val randomId = UUID.randomUUID()
        val friendRequest = FriendRequest(randomId)
        mockMvc.post("/users/friends") {

            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(friendRequest)
            sessionAttrs = mySession
        }.andExpect {
            status { isNotFound() }
            content { contentType(APPLICATION_JSON) }
        }
    }

    @Test
    @WithMockUser
    fun `자기 자신을 요청한 경우 400`() {
        val friendRequest = FriendRequest(user.id)
        mockMvc.post("/users/friends") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(friendRequest)
            sessionAttrs = mySession
        }.andExpect {
            status { isBadRequest() }
            content { contentType(APPLICATION_JSON) }
        }

    }

    @Test
    @WithMockUser
    fun `친구 추가요청 - 정상적으로 요청 - FriendDTO 반환`() {
        val friendRequest = FriendRequest(friend1.id)
        val request = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/users/friends").accept(APPLICATION_JSON)
                .sessionAttrs(mySession).contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(friendRequest))
        )
        request.andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.username").value(user.username))
            .andExpect(jsonPath("$.friendName").value(friend1.username))
            .andDo(
                document(
                    myIdentifier("친구추가"),
                    responseFields(
                        fieldWithPath("id")
                            .description("The ID of the friendship.")
                            .type(STRING),
                        fieldWithPath("username")
                            .description("The username of the member.")
                            .type(STRING),
                        fieldWithPath("friendName")
                            .description("The username of the friend.")
                            .type(STRING)
                    )
                )
            )


    }

    /*
        @GetMapping("/users/friends")
        fun friendsList(@SessionLoginChecker member:MemberDTO): ResponseEntity<List<MemberDTO>> {
            return memberService.getFriends(member.id).let { ResponseEntity.ok().body(it) }
        }
    */
    @Test
    @WithMockUser
    fun `친구 목록 조회 성공`() {
        friendshipRepository.save(Friendship(user, friend1))
        mockMvc.get("/users/friends") {
            accept = APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(APPLICATION_JSON) }
            content {
                jsonPath("$[0].friendName") { value(friend.username) }
                jsonPath("$[0].username") { value(member.username) }
                jsonPath("$[0].id") { isNotEmpty(); isString() }
            }
        }.andDo {
            handle(
                document(
                    myIdentifier("친구목록 조회"),
                    responseFields(
                        fieldWithPath("[].id").type(STRING).description("친구관계 id"),
                        fieldWithPath("[].username").type(STRING).description("유저 id"),
                        fieldWithPath("[].friendName").type(STRING).description("추가한 친구 id")
                    )
                )
            )
        }
    }

}