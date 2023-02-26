package com.example.numblebankingserverchallenge.repository.friendship

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.domain.QFriendship.friendship
import com.example.numblebankingserverchallenge.domain.QMember
import com.example.numblebankingserverchallenge.domain.QMember.member
import com.example.numblebankingserverchallenge.dto.FriendDTO
import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.*

class FriendshipRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : FriendshipRepositoryCustom {
    override fun getFriends(id: UUID): List<Friendship> {
        val member2 = QMember("member2")
        return jpaQueryFactory.select(friendship).from(friendship).join(friendship.friend, member).join(friendship.user, member2).where(member2.id.eq(id)).fetch()
    }

    override fun findFriend(memberId: UUID, friendID: UUID): Friendship? {
        val member2 = QMember("member2")
        return jpaQueryFactory.select(friendship).from(friendship).join(friendship.friend, member).join(friendship.user,member2).where(member2.id.eq(memberId).and(member.id.eq(friendID))).fetchOne()
    }
}