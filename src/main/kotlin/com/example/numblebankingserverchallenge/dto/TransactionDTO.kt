package com.example.numblebankingserverchallenge.dto

import com.example.numblebankingserverchallenge.domain.Transaction
import java.util.*

data class TransactionDTO(val fromAccountId:UUID, val toAccountId:UUID, val amount:Long){
    constructor(transaction:Transaction):this(transaction.fromAccount.id, transaction.toAccount.id, transaction.amount)
}
