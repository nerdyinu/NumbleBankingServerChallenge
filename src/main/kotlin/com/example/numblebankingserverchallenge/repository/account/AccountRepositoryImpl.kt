package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.QAccount.account
import com.example.numblebankingserverchallenge.domain.QMember.member
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import java.util.*

class AccountRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : AccountRepositoryCustom {
    override fun findByIdJoinOwner(accountId: UUID): Account? =
        jpaQueryFactory.select(account).from(account).leftJoin(account.owner, member).on(account.owner.id.eq(member.id))
            .where(account.id.eq(accountId))
            .fetchOne()

    override fun findByOwnerAndId(ownerId: UUID, accountId: UUID): Account? =
        jpaQueryFactory.select(account).from(account).join(account.owner, member).on(member.id.eq(ownerId))
            .where(account.id.eq(accountId))
            .fetchOne()


    override fun findByOwnerId(ownerId: UUID): List<Account> =
        jpaQueryFactory.selectFrom(account).join(account.owner, member)
            .on(member.id.eq(ownerId))
            .fetch()

    override fun findByIdWithLock(accountId: UUID): Account? {
        return jpaQueryFactory.selectFrom(account).where(account.id.eq(accountId)).setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }


}