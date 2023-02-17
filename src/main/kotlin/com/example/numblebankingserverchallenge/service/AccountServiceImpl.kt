package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import com.example.numblebankingserverchallenge.exception.AccountNotFoundException
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.repository.transaction.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val memberRepository: MemberRepository
) : AccountService {
    override fun findAccountById(accountId: UUID): AccountDTO? =
        accountRepository.findByIdJoinOwner(accountId)?.let(::AccountDTO)


    override fun findAllByOwnerId(ownerId: UUID): List<AccountDTO> =
        accountRepository.findByOwnerId(ownerId).map(::AccountDTO)

    @Transactional
    override fun createAccount(ownerId: UUID, name: String, amount: Long): AccountDTO {
        val owner = memberRepository.findById(ownerId).orElseThrow { UserNotFoundException() }
        return accountRepository.save(Account(owner, name,amount)).let(::AccountDTO)
    }

    @Transactional
    override fun createTransaction(fromAccountId: UUID, toAccountId: UUID, amount: Long): TransactionDTO {
        val fromAccount = accountRepository.findByIdWithLock(fromAccountId) ?: throw AccountNotFoundException()
        val toAccount = accountRepository.findByIdWithLock(toAccountId)?: throw AccountNotFoundException()
        val transaction = Transaction(fromAccount, toAccount, amount).let { transactionRepository.save(it) } //shared-lock
        fromAccount.checkAmount(amount) // x-lock : deadlock
        toAccount.addAmount(amount)
        return TransactionDTO(fromAccountId,toAccountId, amount)
    }
}