package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.Transaction
import com.example.numblebankingserverchallenge.dto.AccountDTO
import com.example.numblebankingserverchallenge.dto.TransactionDTO
import java.util.*

interface AccountService {
    fun findAccountById(accountId: UUID):AccountDTO?
    fun findAllByOwnerId(ownerId:UUID):List<AccountDTO>
    fun createAccount(ownerId: UUID, name:String):AccountDTO
    fun createTransaction(fromAccountId:UUID,toAccountId: UUID, amount:Long): TransactionDTO
}