package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.repository.account.AccountRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(MockKExtension::class, SpringExtension::class)
class AccountServiceUnitTest {
    @MockkBean
    lateinit var accountRepository: AccountRepository


}