package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.Version

@Entity
class Account(owner:Member,name:String) :PrimaryKeyEntity(){

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    val owner:Member = owner

    @Column(nullable = false)
    var name:String=name


    @Column(nullable = false)
    private var _balance:Long = 0
        private set
    val balance:Long
        get() = _balance

    @Version
    var versionNo:Long = 0L
}
