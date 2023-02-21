package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import com.example.numblebankingserverchallenge.exception.CustomException
import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.util.*

@ExtendWith(MockKExtension::class, SpringExtension::class)
class AccountServiceUnitTest {
    @MockkBean
    lateinit var accountRepository: AccountRepository


}