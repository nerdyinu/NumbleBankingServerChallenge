package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.*
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.mockapi.NumbleAlarmService
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
    private val memberRepository: MemberRepository,
    private val numbleAlarmService: NumbleAlarmService
) : AccountService {
    override fun findAccountById(accountId: UUID): AccountDTO? =
        accountRepository.findByIdJoinOwner(accountId)?.let(::AccountDTO) ?: throw CustomException.AccountNotFoundException()


    override fun findAllByOwnerId(ownerId: UUID): List<AccountDTO> =
        accountRepository.findByOwnerId(ownerId).map(::AccountDTO)

    @Transactional
    override fun createAccount(ownerId: UUID, accountCreateRequest: AccountCreateRequest): AccountDTO {
        val owner = memberRepository.findById(ownerId).orElseThrow { CustomException.UserNotFoundException() }
        return accountRepository.save(Account(owner, accountCreateRequest.name, accountCreateRequest.amount)).let(::AccountDTO)
    }

    @Transactional
    override fun createTransaction(transactionRequest: TransactionRequest): TransactionDTO {
        val (fromAccountId, toAccountId, amount) = transactionRequest
        val fromAccount = accountRepository.findById(fromAccountId).orElse(null) ?: throw CustomException.AccountNotFoundException()
        val toAccount = accountRepository.findById(toAccountId).orElse(null)?: throw CustomException.AccountNotFoundException()
        val transaction = Transaction(fromAccount, toAccount, amount).let { transactionRepository.save(it) } //shared-lock
        fromAccount.checkAmount(amount) // x-lock : deadlock
        toAccount.addAmount(amount)

        return TransactionDTO(fromAccountId,toAccountId, amount)
    }
}