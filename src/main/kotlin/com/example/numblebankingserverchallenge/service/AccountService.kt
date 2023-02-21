package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.*
import java.util.*

interface AccountService {
    fun findAccountById(accountId: UUID):AccountDTO?
    fun findAllByOwnerId(ownerId:UUID):List<AccountDTO>
    fun createAccount(ownerId: UUID, accountCreateRequest: AccountCreateRequest):AccountDTO
    fun createTransaction(transactionRequest: TransactionRequest): TransactionDTO
}