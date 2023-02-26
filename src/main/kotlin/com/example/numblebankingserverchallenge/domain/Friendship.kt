package com.example.numblebankingserverchallenge.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name="friendship")
class Friendship(    @JsonBackReference
                     @ManyToOne(fetch = FetchType.LAZY, optional = false , cascade = [CascadeType.ALL])
                     @JoinColumn(name="user_id")
                     val user:Member,
                     @JsonBackReference
                     @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
                     @JoinColumn(name="friend_id")
                     val friend:Member
    ):PrimaryKeyEntity() {
    init {
        this.user.addFreind(this)
    }
}
