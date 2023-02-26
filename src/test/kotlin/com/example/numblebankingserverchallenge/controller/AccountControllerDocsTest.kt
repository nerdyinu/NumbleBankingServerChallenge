package com.example.numblebankingserverchallenge.controller

import com.example.numblebankingserverchallenge.*
import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.*
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(RestDocsConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@Transactional
class AccountControllerDocsTest @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,
    private val friendshipRepository: FriendshipRepository,

    ) {
        @Autowired
    lateinit var mockMvc: MockMvc
    lateinit var owner: Member
    lateinit var account1: Account
    lateinit var friend1: Member
    lateinit var friendac: Account
    val signUpRequest = SignUpRequest("inu", "12345value")
    val friendSignup = SignUpRequest("friend1", "23456value")
    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val friend = Member(friendSignup.username, passwordEncoder.encode(friendSignup.pw))
    val returnMember: MemberDTO = MemberDTO(member)
    val mapper = jacksonObjectMapper()
    val mySession = mapOf("user" to returnMember)
    val account = Account(member, "account1", AccountBalance(3000L))
    val friendAccount = Account(friend, "ac2", AccountBalance(3000L))

    fun myIdentifier(methodName: String) = "{class-name}/$methodName"
    val returnAccount = AccountDTO(account)

    @BeforeEach
    fun init() {

        owner = memberRepository.save(member)
        friend1 = memberRepository.save(friend)
        account1 = accountRepository.save(account)
        friendac = accountRepository.save(friendAccount)
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
    fun `without Session - 401 Unauthorized`() {
        mockMvc.get("/accounts/${owner.id}") {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser
    fun `계좌 단건 조회 성공`() {

        val res = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/accounts/{accountId}", account1.id)
                .accept(APPLICATION_JSON)
                .sessionAttrs(mySession)
        )
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
    *
    * */
    @Test
    @WithMockUser
    fun `계좌 목록 조회- fails with an empty session 401 & fails with a wrong id 404`() {
        mockMvc.get("/accounts") {
            accept = APPLICATION_JSON
        }.andExpect { status { isUnauthorized() } }
        mockMvc.get("/accounts") {
            accept = APPLICATION_JSON
            sessionAttrs = mapOf("user" to MemberDTO(UUID.randomUUID(), "hi"))
        }.andExpect { status { isNotFound() } }
    }

    @Test
    @WithMockUser
    fun `계좌목록 조회 성공`() {
        val newAc = accountRepository.save(Account(owner, "ac2"))
        mockMvc.get("/accounts") {
            accept = APPLICATION_JSON
            sessionAttrs = mySession
        }.andExpect {
            status { isOk() }
            content { contentType(APPLICATION_JSON) }
            content {
                jsonPath("$[0].ownerId") { value(owner.id.toString()) }
                jsonPath("$[0].accountId") { value(account1.id.toString()) }
                jsonPath("$[0].name") { value(account1.name) }
                jsonPath("$[0].balance") { value(account1.balance.balance) }
                jsonPath("$[1].ownerId") { value(owner.id.toString()) }
                jsonPath("$[1].accountId") { value(newAc.id.toString()) }
                jsonPath("$[1].name") { value(newAc.name) }
                jsonPath("$[1].balance") { value(newAc.balance.balance) }
            }
        }.andDo {
            handle(
                document(
                    myIdentifier("계좌목록 조회"),

                    responseFields(
                        fieldWithPath("[].ownerId").type(STRING).description("계좌 주인 id"),
                        fieldWithPath("[].accountId").type(STRING).description("계좌 id"),
                        fieldWithPath("[].name").type(STRING).description("계좌명"),
                        fieldWithPath("[].balance").type(NUMBER).description("계좌 잔액")
                    )
                )
            )
        }
    }

    /*
    @PostMapping("/account")
    fun createAccount(@RequestBody accountRequest: AccountCreateRequest, @SessionLoginChecker member:MemberDTO): ResponseEntity<AccountDTO> {
        return accountService.createAccount(member.id, accountRequest).let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }*/

    @Test
    @WithMockUser
    fun `계좌 생성 성공`() {
        val request = AccountCreateRequest("ac2", AccountBalance(3000L))
        val res = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/account").sessionAttrs(mySession).accept(APPLICATION_JSON)
                .contentType(
                    APPLICATION_JSON
                ).content(mapper.writeValueAsString(request))
        )
        res.andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.ownerId").value(owner.id.toString()))
            .andExpect(jsonPath("$.accountId").isString)
            .andExpect(jsonPath("$.name").value(request.name))
            .andExpect(jsonPath("$.balance").value(mapper.writeValueAsString(request.amount)))
            .andDo(
                document(
                    myIdentifier("계좌생성"),
                    requestFields(
                        fieldWithPath("name").type(STRING).description("계좌명"),
                        fieldWithPath("amount").type(ANY).description("계좌 잔액")
                    ),
                    responseFields(
                        fieldWithPath("ownerId").type(STRING).description("계좌 주인 id"),
                        fieldWithPath("accountId").type(STRING).description("계좌 id"),
                        fieldWithPath("name").type(STRING).description("계좌명"),
                        fieldWithPath("balance").type(NUMBER).description("계좌 잔액")
                    )
                )
            )
    }

    @Test
    @WithMockUser
    fun `계좌 생성 -fails with an empty session 401 & fails with a wrong id 404`() {
        val request = AccountCreateRequest("ac2", AccountBalance(3000L))
        mockMvc.post("/account") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isUnauthorized() }
        }
        mockMvc.post("/account") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(request)
            sessionAttrs = mapOf("user" to MemberDTO(UUID.randomUUID(), "hi"))
        }.andExpect {
            status { isNotFound() }
        }
    }


    /*
    @PostMapping("/account/transfer")
    fun transfer(
        @RequestBody transactionRequest: TransactionRequest,
        @SessionLoginChecker member: MemberDTO
    ): ResponseEntity<TransactionDTO> {
        return accountService.createTransaction(transactionRequest).let{ ResponseEntity.status(HttpStatus.OK).body(it)}
    }
    */
    @Test
    @WithMockUser
    fun `계좌 이체 성공`() {
        val friendship = friendshipRepository.save(Friendship(owner, friend1))
        val request = TransactionRequest(account1.id, friendac.id, 3000L)
        val res = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/account/transfer").sessionAttrs(mySession).accept(APPLICATION_JSON)
                .contentType(
                    APPLICATION_JSON
                ).content(mapper.writeValueAsString(request))
        )
        res.andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.fromAccountId").value(account1.id.toString()))
            .andExpect(jsonPath("$.toAccountId").value(friendac.id.toString()))
            .andExpect(jsonPath("$.amount").value(request.amount))
            .andDo(
                document(
                    myIdentifier("계좌이체"),
                    requestFields(
                        fieldWithPath("fromAccountId").type(STRING).description("발신 계좌 id"),
                        fieldWithPath("toAccountId").type(STRING).description("수신 계좌 id"),
                        fieldWithPath("amount").type(NUMBER).description("이체 금액")
                    ),
                    responseFields(
                        fieldWithPath("fromAccountId").type(STRING).description("발신 계좌 id"),
                        fieldWithPath("toAccountId").type(STRING).description("수신 계좌 id"),
                        fieldWithPath("amount").type(NUMBER).description("이체 금무")
                    )
                )
            )
    }

    @Test
    @WithMockUser
    fun `친구 관계가 아닌경우 400 Bad Request`() {
        val request = TransactionRequest(account1.id, friendac.id, 3000L)
        mockMvc.post("/account/transfer") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            sessionAttrs = mySession
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser
    fun `존재 하지 않는 계좌번호 404 Not Found`() {
        val request = TransactionRequest(account1.id, UUID.randomUUID(), 3000L)
        mockMvc.post("/account/transfer") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            sessionAttrs = mySession
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
        }
    }
}