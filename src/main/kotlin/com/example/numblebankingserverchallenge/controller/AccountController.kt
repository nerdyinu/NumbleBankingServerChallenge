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
    @GetMapping("/accounts/{accountId}")
    fun checkBalance(session: HttpSession, @PathVariable("accountId") accountId: UUID): ResponseEntity<AccountDTO> {
        val user =
            session.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return accountService.findAccountById(accountId).let { ResponseEntity.ok().body(it) }
    }

    @PostMapping("/account")
    fun createAccount(session: HttpSession, @RequestBody name: String, @RequestBody amount: Long): ResponseEntity<AccountDTO> {
        val member = session.getAttribute("user") as? MemberDTO ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return accountService.createAccount(member.id, name,amount).let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
    @PostMapping("/account/{fromAccountId}/{toAccountId}")
    fun transfer(
        @PathVariable("fromAccountId") fromAccountId: UUID,
        @PathVariable("toAccountId") toAccountId: UUID,
        @RequestBody amount:Long
    ): ResponseEntity<TransactionDTO> {
        return accountService.createTransaction(fromAccountId,toAccountId,amount).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }

}