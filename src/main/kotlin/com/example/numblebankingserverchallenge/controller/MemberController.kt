package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO
import com.example.numblebankingserverchallenge.dto.UserDTO
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
    fun signup(@RequestBody signUpVO: SignUpVO):ResponseEntity<UserDTO>{
        return memberService.createUser(signUpVO)?.let{ ResponseEntity.ok().body(it)} ?: ResponseEntity.badRequest().build()
    }

    @GetMapping("/login")
    fun login(@RequestBody loginVO:LoginVO, session: HttpSession):ResponseEntity<UserDTO>{
        val userDTO = memberService.login(loginVO) ?: return ResponseEntity.badRequest().build()
        session.setAttribute("user", userDTO)
        return ResponseEntity.ok().body(userDTO)

    }

    @GetMapping("/users/{userId}/friends")
    fun friendsList(@PathVariable("userId") userId:UUID):ResponseEntity<List<UserDTO>>{
        return memberService.getFriends(userId).let { ResponseEntity.ok().body(it) }
    }

    @PostMapping("/users/friends/{friendId}")
    fun addFriend(@PathVariable("friendId") friendId:UUID, httpSession: HttpSession):ResponseEntity<FriendDTO>{
        val user = httpSession.getAttribute("user") as? UserDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val friendDTO=memberService.addFriend(user.id,friendId) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        return ResponseEntity.ok(friendDTO)
    }
}