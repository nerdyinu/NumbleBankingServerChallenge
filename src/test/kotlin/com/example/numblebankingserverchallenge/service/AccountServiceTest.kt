package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.NumbleBankingServerChallengeApplication
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.example.numblebankingserverchallenge.repository.transaction.TransactionRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional


@SpringBootTest(classes=[NumbleBankingServerChallengeApplication::class])
@ActiveProfiles("test")
@Transactional

@ExtendWith(SpringExtension::class)
class AccountServiceTest(
    private val accountService: AccountService,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
}