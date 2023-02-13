package com.example.numblebankingserverchallenge.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Friendship(user:Member, friend:Member):PrimaryKeyEntity() {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name="user_id")
    val user:Member = user

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="friend_id")
    val friend:Member = friend


}
