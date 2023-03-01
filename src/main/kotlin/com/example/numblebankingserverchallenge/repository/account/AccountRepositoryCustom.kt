package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import java.util.*

interface AccountRepositoryCustom {
    fun findByIdWithLock(accountId: UUID, lock:Boolean = false): Account?
    fun findByIdJoinOwner(accountId:UUID, lock:Boolean = false): Account?
    fun findByOwnerAndId(ownerId: UUID,accountId: UUID,lock:Boolean =false):Account?
    fun findAllByOwnerId(ownerId:UUID):List<Account>
}