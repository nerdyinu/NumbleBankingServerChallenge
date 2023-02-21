package com.example.numblebankingserverchallenge.domain

import com.example.numblebankingserverchallenge.dto.AccountBalance
import com.example.numblebankingserverchallenge.exception.CustomException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name="account")
class Account(owner:Member,name:String, balance:AccountBalance = AccountBalance(0L)) :PrimaryKeyEntity(){

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="owner_id")
    val owner:Member = owner

    @Column(nullable = false)
    var name:String=name

    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.ALL])
    private val _transactions:MutableList<Transaction> = mutableListOf()
    val transactions:List<Transaction> get() = _transactions.toList()

    @Column(nullable = false, name = "balance")
    private var _balance:AccountBalance = balance
    val balance:AccountBalance
        get() = _balance

    @Version
    var versionNo:Long = 0L

    fun checkAmount(amount:Long){
        if(_balance.balance<amount)throw CustomException.BadRequestException()
        _balance = AccountBalance(_balance.balance-amount)
    }
    fun addAmount(amount: Long){

        _balance = AccountBalance(_balance.balance+amount)
    }
}
