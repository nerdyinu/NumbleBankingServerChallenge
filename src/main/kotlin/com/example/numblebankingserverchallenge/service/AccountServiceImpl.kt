package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.AccountCreateRequest
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import com.example.numblebankingserverchallenge.dto.TransactionRequest
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.mockapi.NumbleAlarmService
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.example.numblebankingserverchallenge.repository.transaction.TransactionRepository
import jakarta.persistence.OptimisticLockException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository,
    private val numbleAlarmService: NumbleAlarmService,
) : AccountService {
    override fun findAccountByOwnerAndId(ownerId: UUID, accountId: UUID): AccountDTO =
        accountRepository.findByOwnerAndId(ownerId, accountId)?.let(::AccountDTO)
            ?: throw CustomException.AccountNotFoundException()


    override fun findAllByOwnerId(ownerId: UUID): List<AccountDTO> {
        memberRepository.findById(ownerId).orElse(null) ?: throw CustomException.UserNotFoundException()
        return accountRepository.findAllByOwnerId(ownerId).map(::AccountDTO)
    }

    @Transactional
    override fun createAccount(ownerId: UUID, accountCreateRequest: AccountCreateRequest): AccountDTO {
        val owner = memberRepository.findById(ownerId).orElseThrow { CustomException.UserNotFoundException() }
        return accountRepository.save(Account(owner, accountCreateRequest.name, accountCreateRequest.amount))
            .let(::AccountDTO)
    }

    @Transactional
    override fun createTransaction(memberId: UUID, transactionRequest: TransactionRequest): TransactionDTO {
        val (fromAccountId, toAccountId, amount) = transactionRequest
        val fromAccount =
            accountRepository.findByOwnerAndId(memberId, fromAccountId,false)
                                 ?: throw CustomException.AccountNotFoundException()
        val toAccount =
            accountRepository.findByIdJoinOwner(toAccountId, false ) ?: throw CustomException.AccountNotFoundException()
        friendshipRepository.findFriend(fromAccount.owner.id, toAccount.owner.id)
            ?: throw CustomException.BadRequestException()
        try {
            val transaction =
                Transaction(fromAccount, toAccount, amount).let { transactionRepository.save(it) } //shared-lock
            // x-lock : deadlock

            fromAccount.checkAmount(amount)
            toAccount.addAmount(amount)
            numbleAlarmService.notify(fromAccount.owner.id, "transaction completed.")
            return TransactionDTO(fromAccountId, toAccountId, amount)
        }catch (ex:OptimisticLockException){
            println(ex)
            return TransactionDTO(fromAccountId, toAccountId, amount)
        }


    }
}