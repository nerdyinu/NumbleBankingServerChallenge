package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.dto.AccountDTO
import java.util.*

interface AccountService {
    fun findAccountById(accountId: UUID):AccountDTO
}