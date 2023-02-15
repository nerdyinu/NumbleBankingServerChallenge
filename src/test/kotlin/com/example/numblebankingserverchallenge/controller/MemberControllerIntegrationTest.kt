package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.service.MemberService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class MemberControllerIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val memberService: MemberService
) {
//    @PostMapping("/signup")
//    fun signup(@RequestBody signUpRequest: SignUpRequest): ResponseEntity<MemberDTO> {
//        return memberService.createUser(signUpRequest).let{ ResponseEntity.ok().body(it)} ?: ResponseEntity.badRequest().build()
//    }
    @BeforeEach
    fun `회원가입 성공`() {
    }

    @Test
    fun `회원가입 실패-이미 존재하는 회원이름인 경우`(){}

    @Test
    fun `로그인 성공`(){}

    @Test
    fun `로그인 실패 - 비밀번호 오류`(){}

    @Test
    fun `친구 추가 성공 - 회원가입, 로그인 후 정상적으로`(){}

    @Test fun `친구 추가 실패 - 존재하지 않는 친구Id`(){}
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