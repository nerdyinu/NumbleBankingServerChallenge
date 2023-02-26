package com.example.numblebankingserverchallenge.domain

import com.example.numblebankingserverchallenge.dto.AccountBalance
import com.example.numblebankingserverchallenge.exception.CustomException
import jakarta.persistence.*

@Entity
@Table(name = "account")
class Account(
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "owner_id")
    val owner: Member,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false, name = "balance")
    private var _balance: AccountBalance = AccountBalance(0L)
) : PrimaryKeyEntity() {
    init {
        this.owner.addAccount(this)
    }


    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.ALL])
    private val _transactions: MutableList<Transaction> = mutableListOf()
    val transactions: List<Transaction> get() = _transactions.toList()

    val balance: AccountBalance
        get() = _balance

    @Version
    var versionNo: Long = 0L

    fun checkAmount(amount: Long) {
        if (_balance.balance < amount) throw CustomException.BadRequestException()
        _balance = AccountBalance(_balance.balance - amount)
    }

    fun addAmount(amount: Long) {
        _balance = AccountBalance(_balance.balance + amount)
    }
    fun addTransaction(transaction: Transaction){_transactions.add(transaction)}
}
