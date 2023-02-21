package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.config.SessionLoginChecker
import com.example.numblebankingserverchallenge.service.AccountService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.core.ValueClassSupport.boxedValue
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@ExtendWith(SpringExtension::class, MockKExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AccounControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val passwordEncoder: PasswordEncoder,
    @MockkBean val accountService:AccountService
) {

    val member = Member("inu", "encrypted")
    val friend = Member("friend", "encrypted2")
    val returnMember = MemberDTO(member)
    val account = Account(member, "account1", AccountBalance(3000L))
    val friendAccount = Account(friend, "ac2", AccountBalance(3000L))

    val returnAccount = AccountDTO(account)
    val mySession = mapOf("user" to returnMember)
    val mapper = jacksonObjectMapper()
    @BeforeEach
    fun setup(){

    }
    /*@GetMapping("/accounts/{accountId}")
fun checkBalance( @PathVariable("accountId") accountId: UUID, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
    return accountService.findAccountById(accountId).let { ResponseEntity.ok().body(it) }
}*/
    @Test
    @WithMockUser
    fun `checkBalance should return account balance`() {
        every { accountService.findAccountById(account.id) } returns returnAccount
        mockMvc.get("/accounts/${account.id}") {
            accept = MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.accountId") { value(account.id.toString()) }
            jsonPath("$.ownerId") { value(member.id.toString()) }
            jsonPath("$.name") { value(account.name) }
            jsonPath("$.balance") { value(account.balance.balance.toInt()) }
        }
        verify {accountService.findAccountById(account.id)   }
    }

    @Test
    @WithMockUser
    fun `checkBalance - when session doesnt exist return 401 UnAuthorized`(){
        every { accountService.findAccountById(account.id) } returns returnAccount
        mockMvc.get("/accounts/${account.id}"){
            accept= MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized()}
        }
        verify (exactly = 0){  accountService.findAccountById(account.id) }
    }
    /*
*     @PostMapping("/account")
fun createAccount(@RequestBody accountRequest: AccountCreateRequest, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
    return accountService.createAccount(member.id,accountRequest).let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
* */
    @Test
    @WithMockUser
    fun `createAccount - when session doesnt exist should return 401 UnAuthorized`(){
        val accountCreateRequest = AccountCreateRequest( account.name, account.balance)
        every { accountService.createAccount(member.id,accountCreateRequest) } returns returnAccount
        mockMvc.post("/account"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(accountCreateRequest)
        }.andExpect { status { isUnauthorized() } }
        verify(exactly = 0){accountService.createAccount(member.id,accountCreateRequest) }
    }
    @Test
    @WithMockUser
    fun `createAccount - when session exists should return AccountDTO`(){
        val accountCreateRequest = AccountCreateRequest( account.name, account.balance)
        every { accountService.createAccount(member.id,accountCreateRequest) } returns returnAccount
        mockMvc.post("/account"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(accountCreateRequest)
            sessionAttrs = mySession
        }.andExpect {
            status { isOk()}
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.accountId") { value(account.id.toString()) }
            jsonPath("$.ownerId") { value(member.id.toString()) }
            jsonPath("$.name") { value(account.name) }
            jsonPath("$.balance") { value(account.balance.balance.toInt()) }
        }
        verify { accountService.createAccount(member.id,accountCreateRequest) }
    }

    /*@PostMapping("/account/transfer")
    fun transfer(
        @RequestBody transactionRequest: TransactionRequest,
        @SessionLoginChecker member: MemberDTO
    ): ResponseEntity<TransactionDTO> {
        return accountService.createTransaction(transactionRequest).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }
    */
    @Test
    @WithMockUser
    fun `transfer- 세션 없을 시 401 UnAuthorized`(){
        val transactionRequest = TransactionRequest(member.id, friend.id, 3000L )
        val returnTransaction  = TransactionDTO(transactionRequest.fromAccountId,transactionRequest.toAccountId,transactionRequest.amount)
        every { accountService.createTransaction(transactionRequest) } returns returnTransaction
        mockMvc.post("/account/transfer"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(transactionRequest)
        }.andExpect { status { isUnauthorized() } }
        verify(exactly = 0) { accountService.createTransaction(transactionRequest) }
    }
    @Test
    @WithMockUser
    fun `transfer- 세션 있을 시 정상적으로 TransactionDTO 반환`(){
        val transactionRequest = TransactionRequest(account.id, friendAccount.id, 3000L )
        val returnTransaction  = TransactionDTO(transactionRequest.fromAccountId,transactionRequest.toAccountId,transactionRequest.amount)
        every { accountService.createTransaction(transactionRequest) } returns returnTransaction
        mockMvc.post("/account/transfer"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(transactionRequest)
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.fromAccountId"){value(transactionRequest.fromAccountId.toString())}
            jsonPath("$.toAccountId"){value(transactionRequest.toAccountId.toString())}
            jsonPath("$.amount"){value(transactionRequest.amount.toInt())}
        }
        verify { accountService.createTransaction(transactionRequest) }
    }
}