package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import java.util.UUID

interface AccountRepositoryCustom {

    fun findByIdWithLock(accountId: UUID): Account?
    fun findByIdJoinOwner(accountId:UUID): Account?
    fun findByOwnerId(ownerId:UUID):List<Account>
}