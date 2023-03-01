package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.config.SessionLoginChecker
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AccountController(private val accountService: AccountService) {
    @GetMapping("/accounts/{accountId}")
    fun singleAccount(@PathVariable("accountId") accountId: UUID, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
        return accountService.findAccountByOwnerAndId(member.id,accountId).let { ResponseEntity.ok().body(it) }
    }
    @GetMapping("/accounts")
    fun listAccount(@SessionLoginChecker member: MemberDTO):ResponseEntity<List<AccountDTO>>{
        return accountService.findAllByOwnerId(member.id).let { ResponseEntity.ok().body(it) }
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
        return accountService.createTransaction(member.id,transactionRequest).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }

}