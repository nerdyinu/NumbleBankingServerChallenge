package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.config.SessionLoginChecker

import com.example.numblebankingserverchallenge.service.AccountService
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
    fun checkBalance(@PathVariable("accountId") accountId: UUID, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
        return accountService.findAccountById(accountId).let { ResponseEntity.ok().body(it) }
    }

    @PostMapping("/account")
    fun createAccount(@RequestBody accountRequest: AccountCreateRequest, @SessionLoginChecker member:MemberDTO): ResponseEntity<AccountDTO> {
        return accountService.createAccount(member.id, accountRequest).let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
    @PostMapping("/account/transfer")
    fun transfer(
        @RequestBody transactionRequest: TransactionRequest,
        @SessionLoginChecker member: MemberDTO
    ): ResponseEntity<TransactionDTO> {
        return accountService.createTransaction(transactionRequest).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }

}