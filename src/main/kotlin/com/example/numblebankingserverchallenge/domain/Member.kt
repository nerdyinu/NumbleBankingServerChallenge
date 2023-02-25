package com.example.numblebankingserverchallenge.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
@Table(name= "member", indexes = [Index(name="idx_username", columnList = "username")])
class Member(username:String, encryptedPassword:String):PrimaryKeyEntity() {
    @Column(nullable = false, unique = true)
    var username:String =username
        protected set

    var encryptedPassword:String = encryptedPassword

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    @BatchSize(size = 100)
    private val _friends:MutableList<Friendship> = mutableListOf()
    val friends:List<Friendship> get()= _friends.toList()

    @JsonManagedReference
    @OneToMany( mappedBy = "owner", cascade = [CascadeType.ALL])
    @BatchSize(size = 100)
    private val _accounts:MutableList<Account> = mutableListOf()
    val accounts:List<Account> get()=_accounts.toList()

    fun addFreind(friend: Friendship){ _friends.add(friend)}
    fun addAccount(account:Account){_accounts.add(account)}
    override fun toString(): String  = """Member(username= $username, encryptedPassword= $encryptedPassword)"""
}