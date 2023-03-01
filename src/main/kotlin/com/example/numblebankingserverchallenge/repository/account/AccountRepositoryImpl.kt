package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import com.example.numblebankingserverchallenge.domain.QAccount.account
import com.example.numblebankingserverchallenge.domain.QMember.member
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.slf4j.LoggerFactory
import java.util.*

class AccountRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : AccountRepositoryCustom {
    val logger = LoggerFactory.getLogger(AccountRepositoryImpl::class.java)
    override fun findByIdJoinOwner(accountId: UUID, lock: Boolean): Account? {
        val query = jpaQueryFactory.select(account).from(account).join(account.owner, member)
            .where(account.id.eq(accountId))
        if(lock)query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
        return query.fetchOne()
    }


    override fun findByOwnerAndId(ownerId: UUID, accountId: UUID, lock:Boolean): Account? {
     val query=  jpaQueryFactory.select(account).from(account).join(account.owner, member)
            .where(member.id.eq(ownerId).and(account.id.eq(accountId)))
        if(lock)query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
        return query.fetchOne()
    }


    override fun findAllByOwnerId(ownerId: UUID): List<Account> =
        jpaQueryFactory.selectFrom(account).join(account.owner, member)
            .on(member.id.eq(ownerId))
            .fetch()

    override fun findByIdWithLock(accountId: UUID, lock: Boolean): Account? {
        val query= jpaQueryFactory.selectFrom(account).where(account.id.eq(accountId))
        if(lock)query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
        return query.fetchOne()
    }


}