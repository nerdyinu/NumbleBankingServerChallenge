package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import java.util.*

interface AccountRepositoryCustom {

    fun findByIdWithLock(accountId: UUID): Account?
    fun findByIdJoinOwner(accountId:UUID): Account?
    fun findByOwnerAndId(ownerId: UUID,accountId: UUID):Account?
    fun findAllByOwnerId(ownerId:UUID):List<Account>
}