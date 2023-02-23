package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.NumbleBankingServerChallengeApplication
import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.repository.transaction.TransactionRepository
import kotlinx.coroutines.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional


@SpringBootTest(classes=[NumbleBankingServerChallengeApplication::class])
@ActiveProfiles("test")
@Transactional

@ExtendWith(SpringExtension::class)
class AccountServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val accountService: AccountService,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    lateinit var owner:Member
    lateinit var account: Account
    @BeforeEach
    fun init() {
        owner = memberRepository.save(Member("inu","encrypted"))
        account= accountRepository.save(Account(owner,"ac1"))
    }
    @AfterEach
    fun delete(){
        memberRepository.deleteAll()
    }
    /*
    * fun createAccount(ownerId: UUID, name:String):AccountDTO
    * */
    @Test
    fun `should create an account`(){
        val dto=accountService.createAccount(owner.id, AccountCreateRequest("ac2", AccountBalance(0L)))
        val ac=accountRepository.findById(dto.accountId).orElse(null)
        assertThat(ac).isNotNull
        assertThat(ac.id).isEqualTo(dto.accountId)
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
        val dto = accountService.findAccountById(account.id)
        assertThat(dto).isNotNull
        assertThat(account.name).isEqualTo(dto?.name)
        assertThat(account.balance).isEqualTo(dto?.balance)
    }
    /*
    *fun findAllByOwnerId(ownerId:UUID):List<AccountDTO>
    * */
    @Test
    fun `should return all accounts by ownerId`(){
//        val ac1=accountRepository.save(Account(owner, "ac1"))
        val ac2=accountRepository.save(Account(owner, "ac2"))
        val list=accountService.findAllByOwnerId(owner.id)
        assertThat(list).containsExactly(AccountDTO(account), AccountDTO(ac2))
    }
    /*
    * fun createTransaction(fromAccountId:UUID,toAccountId: UUID, amount:Long): TransactionDTO
    * */
    @Test
    @Transactional
    fun `should createTransaction in concurrent env`(){
        lateinit var res:List<TransactionDTO>
        val friend = memberRepository.save(Member("friend", "ecpw1"))
        val ac1 = accountRepository.save(Account(owner,"ac1",AccountBalance(3000L)))
        val friendac = accountRepository.save(Account(friend,"friendac",AccountBalance(3000L)))

        runBlocking {
            val job1= async {
                accountService.createTransaction(TransactionRequest(ac1.id, friendac.id, 1000 ))
            }

            val job2=async {
                accountService.createTransaction(TransactionRequest(ac1.id,friendac.id,2000))
            }
            res=awaitAll(job1,job2)
        }
        val res1=accountRepository.findById(ac1.id).orElse(null)
        val res2=accountRepository.findById(friendac.id).orElse(null)
        assertThat(res1).isNotNull; assertThat(res2).isNotNull
        assertThat(res1.balance.balance).isEqualTo(0L)
        assertThat(res2.balance.balance).isEqualTo(6000)
        assertThat(res).extracting("fromAccountId").containsOnly(ac1.id)
        assertThat(res).extracting("toAccountId").containsOnly(friendac.id)
        assertThat(res).extracting("amount").containsExactly(1000L,2000L)
    }
}