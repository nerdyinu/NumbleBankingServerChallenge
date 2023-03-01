package com.example.numblebankingserverchallenge.repository


import com.example.numblebankingserverchallenge.JpaTestConfig
import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.*

@DataJpaTest
@Import(JpaTestConfig::class)
@Transactional
class FriendshipRepositoryUnitTest @Autowired constructor(
    private val entityManager: TestEntityManager,
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository
) {
    /*
    * fun getFriends(id: UUID):List<Member>
    * */
    @Test
    fun `test getFriends()`() {
        val member = Member("inu", "encrypted")
        val friend = Member("friend1", "encrypted1")
        val friend2 = Member("friend2", "encrypted2")
        entityManager.persist(member)
        entityManager.persist(friend)
        entityManager.persist(friend2)
        val friendship = Friendship(member, friend)
        val friendship2 = Friendship(member, friend2)

        entityManager.persist(friendship)
        entityManager.persist(friendship2)
        entityManager.flush()

        val res = friendshipRepository.getFriends(member.id)
        assertThat(res).extracting("friend").extracting("id").containsOnly(friend.id, friend2.id)
        assertThat(res).extracting("friend").extracting("username").containsOnly(friend.username, friend2.username)

    }

    //    fun findFriend(memberId: UUID, friendID: UUID):Friendship?
    @Test
    fun `test findFriend`() {
        val member = Member("inu", "encrypted")
        val friend = Member("friend1", "encrypted1")
        val friend2 = Member("friend2", "encrypted2")
        entityManager.persist(member)
        entityManager.persist(friend)
        entityManager.persist(friend2)
        val friendship = Friendship(member, friend)
        val friendship2 = Friendship(member, friend2)

        entityManager.flush()
        val findfriend = friendshipRepository.findFriend(member.id, friend.id)
        val findfriend2 = friendshipRepository.findFriend(member.id, friend2.id)
        assertThat(findfriend?.id).isEqualTo(friendship.id)
        assertThat(findfriend2?.id).isEqualTo(friendship2.id)
    }
}