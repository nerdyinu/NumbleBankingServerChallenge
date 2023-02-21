package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.config.SessionLoginChecker
import com.example.numblebankingserverchallenge.service.AccountService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
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
    fun `세션이 존재한다면 정상적으로 AccountDTO 반환`(){
        every { accountService.findAccountById(account.id) } returns returnAccount
        mockMvc.get("/accounts/${account.id}"){
            accept= MediaType.APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content{ json(mapper.writeValueAsString(returnAccount))}
        }
    }

    @Test
    @WithMockUser
    fun `checkBalance - 세션이 존재하지 않는다면 401 UnAuthorized`(){
        every { accountService.findAccountById(account.id) } returns returnAccount
        mockMvc.get("/accounts/${account.id}"){
            accept= MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized()}
        }
    }
    /*
*     @PostMapping("/account")
fun createAccount(@RequestBody accountRequest: AccountCreateRequest, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
    return accountService.createAccount(member.id,accountRequest).let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
* */
    @Test
    @WithMockUser
    fun `createAccount - 세션이 존재하지 않는 경우 401 UnAuthorized`(){
        val accountCreateRequest = AccountCreateRequest( account.name, account.balance)
        every { accountService.createAccount(member.id,accountCreateRequest) } returns returnAccount
        mockMvc.post("/account"){
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(accountCreateRequest)
        }.andExpect { status { isUnauthorized() } }
    }
    @Test
    @WithMockUser
    fun `createAccount - 세션이 존재하는 경우 AccountDTO 반환`(){
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
            content{ json(mapper.writeValueAsString(returnAccount))}
        }
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
    }
    @Test
    @WithMockUser
    fun `transfer- 세션 있을 시 정상적으로 TransactionDTO 반환`(){
        val transactionRequest = TransactionRequest(member.id, friend.id, 3000L )
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
            content { json(mapper.writeValueAsString(returnTransaction)) }
        }
    }
}