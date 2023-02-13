package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountServiceImpl(private val accountRepository: AccountRepository) :AccountService{
    override fun findAccountById(accountId: UUID): AccountDTO {
        TODO("Not yet implemented")
    }
}