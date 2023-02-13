package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountServiceImpl(private val accountRepository: AccountRepository, private val memberRepository: MemberRepository) : AccountService {
    override fun findAccountById(accountId: UUID): AccountDTO? =
        accountRepository.findAccountJoinOwner(accountId)?.let(::AccountDTO)


    override fun findAllByOwnerId(ownerId: UUID): List<AccountDTO> =
        accountRepository.findByOwnerId(ownerId).map(::AccountDTO)

    override fun createAccount(ownerId: UUID, name: String): AccountDTO {
        val owner = memberRepository.findById(ownerId).orElseThrow { UserNotFoundException() }
        return accountRepository.save(Account(owner, name)).let(::AccountDTO)
    }
}