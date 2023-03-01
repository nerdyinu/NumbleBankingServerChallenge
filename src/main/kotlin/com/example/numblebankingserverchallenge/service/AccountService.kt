package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.dto.AccountCreateRequest
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import com.example.numblebankingserverchallenge.dto.TransactionRequest
import java.util.*

interface AccountService {
    fun findAccountByOwnerAndId(ownerId: UUID,accountId: UUID):AccountDTO?
    fun findAllByOwnerId(ownerId:UUID):List<AccountDTO>
    fun createAccount(ownerId: UUID, accountCreateRequest: AccountCreateRequest):AccountDTO
    fun createTransaction(memberId:UUID,transactionRequest: TransactionRequest): TransactionDTO
}