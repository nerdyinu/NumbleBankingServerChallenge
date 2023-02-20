package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name="account")
class Account(owner:Member,name:String, balance:Long = 0L) :PrimaryKeyEntity(){

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="owner_id")
    val owner:Member = owner

    @Column(nullable = false)
    var name:String=name

    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.ALL])
    private val _transactions:MutableList<Transaction> = mutableListOf()
    val transactions:List<Transaction> get() = _transactions.toList()

    @Column(nullable = false)
    private var _balance:Long = balance
    val balance:Long
        get() = _balance

    @Version
    var versionNo:Long = 0L

    fun checkAmount(amount:Long){_balance-=amount}
    fun addAmount(amount: Long){_balance+=amount}
}
