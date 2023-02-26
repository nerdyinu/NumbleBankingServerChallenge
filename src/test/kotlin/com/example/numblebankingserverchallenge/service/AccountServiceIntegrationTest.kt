package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.NumbleBankingServerChallengeApplication
import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional


@SpringBootTest(classes=[NumbleBankingServerChallengeApplication::class])
@ActiveProfiles("test")
@Transactional

@ExtendWith(SpringExtension::class)
class AccountServiceIntegrationTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val accountService: AccountService,
    private val accountRepository: AccountRepository,
    private val friendshipRepository: FriendshipRepository
) {
    lateinit var owner:Member
    lateinit var account1: Account
    lateinit var friend1:Member
    lateinit var friendac:Account
    val member = Member(signUpRequest.username, passwordEncoder.encode(signUpRequest.pw))
    val friend = Member(friendSignup.username, passwordEncoder.encode(friendSignup.pw))

    val account = Account(member, "account1", AccountBalance(3000L))
    val friendAccount = Account(friend, "ac2", AccountBalance(3000L))

    @BeforeEach
    fun init() {
        owner = memberRepository.save(member)
        friend1 = memberRepository.save(friend)
        account1= accountRepository.save(account)
         friendac= accountRepository.save(friendAccount)
    }
    @AfterEach
    fun delete(){
        memberRepository.deleteAll()
        accountRepository.deleteAll()
    }
    /*
    * fun createAccount(ownerId: UUID, name:String):AccountDTO
    * */
    @Test
    fun `should create an account`(){
        val dto=accountService.createAccount(owner.id, AccountCreateRequest("ac2", AccountBalance(0L)))
        val ac=accountRepository.findByOwnerAndId(dto.ownerId,dto.accountId)
        assertThat(ac).isNotNull
        assertThat(ac!!.id).isEqualTo(dto.accountId)
        assertThat(ac.owner).isEqualTo(owner)
        assertThat(ac.name).isEqualTo(dto.name)
    }

    /*
    *  fun findAccountById(accountId: UUID):AccountDTO?
    *
    * */
    @Test
    fun `should find an account by id`(){
//        val findOwner= memberRepository.findById(owner.id).orElseThrow()
        val dto = accountService.findAccountByOwnerAndId(owner.id,account1.id)
        assertThat(dto).isNotNull
        assertThat(dto!!.name).isEqualTo(account1.name)
        assertThat(dto.balance).isEqualTo(account1.balance)
    }
    /*
    *fun findAllByOwnerId(ownerId:UUID):List<AccountDTO>
    * */
    @Test
    fun `should return all accounts by ownerId`(){
//        val ac1=accountRepository.save(Account(owner, "ac1"))
        val ac2=accountRepository.save(Account(owner, "ac2"))
        val list=accountService.findAllByOwnerId(owner.id)
        assertThat(list).containsExactly(AccountDTO(account1), AccountDTO(ac2))
    }
    /*
    * fun createTransaction(fromAccountId:UUID,toAccountId: UUID, amount:Long): TransactionDTO
    * */
    @Test
    @Transactional
    fun `should createTransaction in concurrent env`(){
        lateinit var res:List<TransactionDTO>

//        val ac1 = accountRepository.save(Account(owner,"ac1",AccountBalance(3000L)))

        friendshipRepository.save(Friendship(owner,friend1))
        runBlocking {
            val job1= async {
                accountService.createTransaction(TransactionRequest(account1.id, friendac.id, 1000 ))
            }

            val job2=async {
                accountService.createTransaction(TransactionRequest(account1.id,friendac.id,2000))
            }
            res=awaitAll(job1,job2)
        }
        val res1=accountRepository.findById(account1.id).orElse(null)
        val res2=accountRepository.findById(friendac.id).orElse(null)
        assertThat(res1).isNotNull; assertThat(res2).isNotNull
        assertThat(res1.balance.balance).isEqualTo(0L)
        assertThat(res2.balance.balance).isEqualTo(6000)
        assertThat(res).extracting("fromAccountId").containsOnly(account1.id)
        assertThat(res).extracting("toAccountId").containsOnly(friendac.id)
        assertThat(res).extracting("amount").containsExactly(1000L,2000L)
    }
}