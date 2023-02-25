package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.NUMBER
import com.example.numblebankingserverchallenge.NumbleBankingServerChallengeApplication
import com.example.numblebankingserverchallenge.RestDocsConfig
import com.example.numblebankingserverchallenge.STRING
import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.AccountBalance
import com.example.numblebankingserverchallenge.dto.MemberDTO
import com.example.numblebankingserverchallenge.dto.SignUpRequest
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.service.AccountService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import com.example.numblebankingserverchallenge.util.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(RestDocsConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@Transactional
class AccountControllerDocsTest @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    private val accountService: AccountService,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,

    ) {
    @Autowired
    lateinit var mockMvc: MockMvc
    lateinit var owner:Member
    lateinit var account1: Account
    lateinit var friend1:Member
    lateinit var friendac:Account
    @BeforeEach
    fun init() {
        owner = memberRepository.save(member)
        friend1 = memberRepository.save(friend)
        account1= accountRepository.save(account)
        friendac= accountRepository.save(friendAccount)
    }

    @AfterEach
    fun deleteAll() {
        memberRepository.deleteAll()
        accountRepository.deleteAll()
    }

    /*
    * @GetMapping("/accounts/{accountId}")
    fun singleAccount(@PathVariable("accountId") accountId: UUID, @SessionLoginChecker member: MemberDTO): ResponseEntity<AccountDTO> {
        return accountService.findAccountByOwnerAndId(member.id,accountId).let { ResponseEntity.ok().body(it) }
    }
    * */
    @Test
    @WithMockUser
    fun `존재하지 않는 accountId - 404 Not Found`() {


        mockMvc.get("/accounts/${UUID.randomUUID()}") {
            accept = APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser
    fun `계좌 단건 조회 성공`() {

        val res = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/accounts/{accountId}", account1.id)
                .accept(APPLICATION_JSON)
                .sessionAttrs(mySession))
        res.andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.ownerId").value(owner.id.toString()))
            .andExpect(jsonPath("$.accountId").value(account1.id.toString()))
            .andExpect(jsonPath("$.name").value(account1.name))
            .andExpect(jsonPath("$.balance").value(account1.balance.balance))
            .andDo(
                document(
                    myIdentifier("계좌조회"),
                    pathParameters(parameterWithName("accountId").description("계좌 id")),
                    responseFields(
                        fieldWithPath("ownerId").type(STRING).description("계좌주 id"),
                        fieldWithPath("accountId").type(STRING).description("계좌 id"),
                        fieldWithPath("name").type(STRING).description("계좌명"),
                        fieldWithPath("balance").type(NUMBER).description("계좌 잔액")
                    )
                )
            )

    }

    /*
    *  @GetMapping("/accounts")
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
        return accountService.createTransaction(transactionRequest).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }
    *
    * */

}