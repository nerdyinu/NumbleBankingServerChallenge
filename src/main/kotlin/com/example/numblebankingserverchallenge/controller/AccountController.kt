package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.UserDTO
import com.example.numblebankingserverchallenge.service.AccountService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AccountController(private val accountService: AccountService) {
    @PostMapping("/{accountId}")
    fun checkBalance(session: HttpSession, accountId:UUID):ResponseEntity<AccountDTO>{
        val user = session.getAttribute("user") as? UserDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return accountService.findAccountById(accountId).let { ResponseEntity.ok().body(it) }
    }
}