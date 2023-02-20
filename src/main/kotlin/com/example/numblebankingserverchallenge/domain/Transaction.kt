package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.*

@Entity
@Table(name="transaction")
class Transaction(fromAccount:Account,toAccount:Account,checkAmount:Long) :PrimaryKeyEntity(){



    @Column(nullable=false)
    val amount:Long = checkAmount

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="from_account_id")
    val fromAccount:Account = fromAccount

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="to_account_id")
    val toAccount:Account = toAccount

}