package com.example.numblebankingserverchallenge.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

@Entity
class Member(username:String):PrimaryKeyEntity() {
    @Column(nullable = false)
    var username:String =username
        protected set

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private val _friends:MutableList<FriendShip> = mutableListOf()
    val friends:List<FriendShip> get()= _friends.toList()

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private val _accounts:MutableList<Account> = mutableListOf()
    val accounts get()=_accounts.toList()
    fun addFreind(friend: FriendShip){ _friends.add(friend)}

}