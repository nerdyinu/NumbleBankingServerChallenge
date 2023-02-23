package com.example.numblebankingserverchallenge.repository


import com.example.numblebankingserverchallenge.JpaTestConfig
import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.AccountBalance
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import kotlinx.coroutines.*
import org.assertj.core.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.lang.Runnable


@Component
class AsyncTransaction {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun run(runnable: Runnable) {
        runnable.run()
    }
}

@DataJpaTest
@ExtendWith(SpringExtension::class)
@Import(JpaTestConfig::class)
@ActiveProfiles("test")
@Transactional
class AccountRepositoryUnitTest @Autowired constructor(
    private val em: TestEntityManager,
    private val accountRepository: AccountRepository,
    private val transactionManager: PlatformTransactionManager
) {

    @PersistenceUnit
    lateinit var emf: EntityManagerFactory
    val executorService = Executors.newFixedThreadPool(2)
    val latch = CountDownLatch(2)
    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (ex: InterruptedException) {
            throw RuntimeException(ex)
        }
    }

    /*
    *
    *
    fun findByOwnerId(ownerId:UUID):List<Account>
    *
    * */
    @Test
    fun `test findByOwnerId`() {
        val owner = Member("inu", "encrypted")
        val account = Account(owner, "account1",)
        val account2 = Account(owner, "account2")
        em.persist(owner)
        em.persist(account)
        em.persist(account2)
        em.flush()
        val res = accountRepository.findByOwnerId(owner.id)
        assertThat(res[0].id).isEqualTo(account.id)
        assertThat(res[1].id).isEqualTo(account2.id)
    }

    /*
    *  fun findByIdJoinOwner(accountId:UUID): Account?
    * */
    @Test
    fun `test findByIdFetchOWner`() {
        val owner = Member("inu", "encrypted")
        val account = Account(owner, "account1")
        val account2 = Account(owner, "account2")
        em.persist(owner)
        em.persist(account)
        em.persist(account2)
        em.flush()

        val res = accountRepository.findByIdJoinOwner(account.id)
        assertThat(res?.owner?.id).isEqualTo(owner.id)
        val res2 = accountRepository.findByIdJoinOwner(account2.id)
        assertThat(res2?.owner?.id).isEqualTo(owner.id)
    }

    /*
    fun findByIdWithLock(accountId: UUID): Account?
    Test LockTimeOutException
    */
    @Test
    fun `test findByIdWithLock`() {
        runBlocking {
            val member = Member("inu", encryptedPassword = "encrypted")
            val account = Account(member, "account1", AccountBalance(0L))
            em.persist(member)
            em.persist(account)
            em.flush()

            val threadLocalEntityManager = ThreadLocal<EntityManager>()
            val deferred1 = async {
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findByIdWithLock(account.id)
                    account1?.addAmount(5000L)
                }
            }

            val deferred2 = async{
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findByIdWithLock(account.id)
                    account1?.checkAmount(3000L)
                }
            }

//            assertThatThrownBy { runBlocking { deferred1.await();deferred2.await(); } }
            awaitAll(deferred1,deferred2)
            assertThat(account.balance.balance).isEqualTo(2000L)
        }
    }

    @Test
    fun  `test findById does not trigger deadlock`() {

        val member = Member("inu", encryptedPassword = "encrypted")
        val account = Account(member, "account1",AccountBalance(0L))
        em.persist(member)
        em.persist(account)
        em.flush()
        runBlocking {
            val deferred1 = async {
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findByIdWithLock(account.id)
                    account.addAmount(3000)
                }

            }
            val deferred2 = async {
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findByIdWithLock(account.id)
                    account.addAmount(3000)
                }
            }
           awaitAll(deferred1,deferred2)
            assertThat(em.find(Account::class.java, account.id).balance.balance).isEqualTo(6000L)
        }

    }
    @Test
    fun `trying to check amount from a zero-balance account occurs exception`(){
        val member = Member("inu", encryptedPassword = "encrypted")
        val account = Account(member, "account1",AccountBalance(0L))
        em.persist(member)
        em.persist(account)
        em.flush()
        assertThrows<CustomException.BadRequestException> {  accountRepository.findByIdWithLock(account.id)?.checkAmount(3000)}
    }
}