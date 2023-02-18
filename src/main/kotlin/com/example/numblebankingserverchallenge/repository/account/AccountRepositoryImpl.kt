package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.QAccount
import com.example.numblebankingserverchallenge.domain.QAccount.*
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QMember.*
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import java.util.UUID

class AccountRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : AccountRepositoryCustom {
    override fun findByIdJoinOwner(accountId: UUID): Account? =
        jpaQueryFactory.select(account).from(account).leftJoin(account.owner, member).fetchJoin()
            .where(account.id.eq(accountId))
            .fetchOne()


    override fun findByOwnerId(ownerId: UUID): List<Account> =
        jpaQueryFactory.selectFrom(account).leftJoin(account.owner, member).fetchJoin()
            .where(member.id.eq(ownerId))
            .fetch()

    override fun findByIdWithLock(accountId: UUID): Account? {

        return jpaQueryFactory.selectFrom(account).where(account.id.eq(accountId)).setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }


}