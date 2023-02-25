package com.example.numblebankingserverchallenge.repository.transaction

import com.example.numblebankingserverchallenge.domain.Transaction
import java.util.*

interface TransactionRepositoryCustom {
    // generate transaction
    //
//    fun addTransaction(transaction: Transaction)
    fun findByIdFetchAccounts(transactionId:UUID):Transaction?
    fun findByOwnerId(ownerId:UUID):List<Transaction>
    fun findByReceiverId(receiverId:UUID):List<Transaction>
}