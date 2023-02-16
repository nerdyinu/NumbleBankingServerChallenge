package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import jakarta.persistence.PessimisticLockException
import kotlinx.coroutines.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.junit.jupiter.api.assertThrows
import org.junit.platform.suite.api.ExcludePackages
import org.springframework.dao.PessimisticLockingFailureException
import java.lang.Exception
import java.lang.Runnable
import java.util.concurrent.TimeUnit


@Component
class AsyncTransaction {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun run(runnable: Runnable) {
        runnable.run()
    }
}

@DataJpaTest
@Import(AsyncTransaction::class)
class AccountRepositoryUnitTest @Autowired constructor(
    private val asyncTransaction: AsyncTransaction,
    private val em: TestEntityManager,
    private val accountRepository: AccountRepository,
    private val transactionManager: PlatformTransactionManager
) {
    @PersistenceUnit
    lateinit var emf:EntityManagerFactory
    val executorService = Executors.newFixedThreadPool(2)
    val latch = CountDownLatch(2)
    fun sleep(millis:Long){
        try{Thread.sleep(millis)}catch (ex:InterruptedException){throw RuntimeException(ex)}
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
        val account = Account(owner, "account1")
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
    Test Deadlock
    */
    @Test
    fun `test findByIdWithLock`() = runBlocking {
        val member = Member("inu", encryptedPassword = "encrypted")
        val account = Account(member, "account1")
        em.persist(member)
        em.persist(account)
        em.flush()
        val threadLocalEntityManager = ThreadLocal<EntityManager>()
        val job1 = launch {
            val entityManager = emf.createEntityManager()
            threadLocalEntityManager.set(entityManager)

            val tr = entityManager.transaction
            tr.begin()
            val account1 = accountRepository.findByIdWithLock(account.id)
            delay(5000)
            tr.commit()

            entityManager.close()
        }



        val job2 = launch{
            val entityManager = emf.createEntityManager()
            threadLocalEntityManager.set(entityManager)

            assertThrows<PessimisticLockException> {
                val tr = entityManager.transaction
                tr.begin()
                val account2 = accountRepository.findByIdWithLock(account.id)
                tr.commit()
            }

            entityManager.close()
        }
        joinAll(job1, job2)
    }
    @Test
    fun `test findById does not trigger deadlock`(){

            val member = Member("inu", encryptedPassword = "encrypted")
            val account = Account(member, "account1")
            em.persist(member)
            em.persist(account)
            em.flush()
            executorService.submit {
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findById(account.id)

                    latch.countDown()
                }

            }
            executorService.submit{
                val transactionTemplate = TransactionTemplate(transactionManager)
                transactionTemplate.execute {
                    val account1 = accountRepository.findById(account.id)
                    latch.countDown()
                }
            }
            latch.await()
        assertThat(executorService.isShutdown).isEqualTo(true)
    }

}