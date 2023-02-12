package com.example.numblebankingserverchallenge.domain

import jakarta.persistence.Entity
import jakarta.persistence.OneToOne

@Entity
class Transaction(relatinoship:FriendShip) :PrimaryKeyEntity(){

    @OneToOne
    val relationship:FriendShip=relatinoship
}