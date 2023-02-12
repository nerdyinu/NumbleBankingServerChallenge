package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class Transaction(relationship:FriendShip,checkAmount:Long) :PrimaryKeyEntity(){

    @OneToOne
    @JoinColumn(name="id")
    val relationship:FriendShip=relationship

    @Column(nullable=false)
    val checkAmount:Long = checkAmount


}