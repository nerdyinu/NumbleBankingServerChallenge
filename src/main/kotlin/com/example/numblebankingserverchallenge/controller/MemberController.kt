package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginRequest
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.service.MemberService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class MemberController (private val memberService: MemberService){
    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequest: SignUpRequest):ResponseEntity<MemberDTO>{
        return memberService.createUser(signUpRequest)?.let{ ResponseEntity.ok().body(it)} ?: ResponseEntity.badRequest().build()
    }

    @GetMapping("/login")
    fun login(@RequestBody loginRequest:LoginRequest, session: HttpSession):ResponseEntity<MemberDTO>{
        val MemberDTO = memberService.login(loginRequest) ?: return ResponseEntity.badRequest().build()
        session.setAttribute("user", MemberDTO)
        return ResponseEntity.ok().body(MemberDTO)

    }

    @GetMapping("/users/{userId}/friends")
    fun friendsList(@PathVariable("userId") userId:UUID):ResponseEntity<List<MemberDTO>>{
        return memberService.getFriends(userId).let { ResponseEntity.ok().body(it) }
    }

    @PostMapping("/users/friends/{friendId}")
    fun addFriend(@PathVariable("friendId") friendId:UUID, httpSession: HttpSession):ResponseEntity<FriendDTO>{
        val user = httpSession.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val friendDTO=memberService.addFriend(user.id,friendId) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        return ResponseEntity.ok(friendDTO)
    }
}