package com.example.numblebankingserverchallenge.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import org.hibernate.annotations.BatchSize

@Entity
class Member(username:String, encryptedPassword:String?):PrimaryKeyEntity() {
    @Column(nullable = false, unique = true)
    var username:String =username
        protected set

    var encryptedPassword:String? = encryptedPassword

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    private val _friends:MutableList<Friendship> = mutableListOf()
    val friends:List<Friendship> get()= _friends.toList()

    @JsonManagedReference
    @OneToMany( mappedBy = "owner", cascade = [CascadeType.ALL])
    @BatchSize(size = 100)
    private val _accounts:MutableList<Account> = mutableListOf()
    val accounts get()=_accounts.toList()

    fun addFreind(friend: Friendship){ _friends.add(friend)}

}