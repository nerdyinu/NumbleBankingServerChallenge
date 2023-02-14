package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import com.example.numblebankingserverchallenge.service.AccountService
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
class AccountController(private val accountService: AccountService) {
    @GetMapping("/{userId}/{accountId}")
    fun checkBalance(session: HttpSession, @PathVariable("accountId") accountId: UUID): ResponseEntity<AccountDTO> {
        val user =
            session.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return accountService.findAccountById(accountId).let { ResponseEntity.ok().body(it) }
    }

    @PostMapping("/{userId}/account")
    fun createAccount(@PathVariable("userId") userId: UUID, @RequestBody name: String): ResponseEntity<AccountDTO> =
        accountService.createAccount(userId, name).let { ResponseEntity.status(HttpStatus.OK).body(it) }

    @PostMapping("/{userId}/{fromAccountId}/{toAccountId}")
    fun transfer(
        @PathVariable("userId") userId: UUID,
        @PathVariable("fromAccountId") fromAccountId: UUID,
        @PathVariable("toAccountId") toAccountId: UUID,
        @RequestBody amount:Long
    ): ResponseEntity<TransactionDTO> {
        return accountService.createTransaction(fromAccountId,toAccountId,amount).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }

}