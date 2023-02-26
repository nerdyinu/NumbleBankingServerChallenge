package com.example.numblebankingserverchallenge.repository.transaction

import com.example.numblebankingserverchallenge.domain.QAccount
import com.example.numblebankingserverchallenge.domain.QAccount.account
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QTransaction.transaction
import com.example.numblebankingserverchallenge.domain.Transaction
import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.*

class TransactionRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):TransactionRepositoryCustom {

    override fun findByIdFetchAccounts(transactionId: UUID):Transaction? {
        val account2 = QAccount("account2")
        return jpaQueryFactory.selectFrom(transaction).where(transaction.id.eq(transactionId)).leftJoin(transaction.fromAccount, account).fetchJoin().leftJoin(transaction.toAccount, account2).fetchJoin().fetchOne()
    }

    override fun findByOwnerId(ownerId: UUID): List<Transaction> {
        return jpaQueryFactory.selectFrom(transaction).join(transaction.fromAccount, account).where(
            transaction.fromAccount.owner.id.eq(ownerId)).leftJoin(transaction.fromAccount.owner, QMember.member).fetchJoin().fetch()
    }


    override fun findByReceiverId(receiverId: UUID): List<Transaction> {
        return jpaQueryFactory.selectFrom(transaction).join(transaction.toAccount, account).on(
            transaction.toAccount.owner.id.eq(receiverId)).leftJoin(transaction.toAccount.owner, QMember.member).fetchJoin().fetch()
    }
}