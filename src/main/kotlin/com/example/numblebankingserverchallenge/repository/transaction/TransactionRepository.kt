package com.example.numblebankingserverchallenge.repository.transaction

import com.example.numblebankingserverchallenge.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TransactionRepository :JpaRepository<Transaction, UUID>, TransactionRepositoryCustom{
}