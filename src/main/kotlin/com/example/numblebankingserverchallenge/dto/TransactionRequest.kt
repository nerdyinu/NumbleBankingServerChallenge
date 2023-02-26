package com.example.numblebankingserverchallenge.dto

import java.io.Serializable
import java.util.*

data class TransactionRequest(val fromAccountId:UUID, val toAccountId:UUID, val amount:Long):Serializable{
}