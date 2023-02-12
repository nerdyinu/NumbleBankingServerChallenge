package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FriendshipRepository:JpaRepository<Friendship, UUID> {
}