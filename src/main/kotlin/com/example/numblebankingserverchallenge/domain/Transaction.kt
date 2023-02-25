package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.*

@Entity
@Table(name = "transaction")
class Transaction(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id")
    val fromAccount: Account,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_account_id")
    val toAccount: Account,
    @Column(nullable = false)
    val amount: Long
) : PrimaryKeyEntity() {
    init {
        this.fromAccount.addTransaction(this)
    }

}