package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.friendship.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class FriendshipRepositoryUnitTest @Autowired constructor(
    private val entityManager: TestEntityManager,
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository
) {
    /*
    * fun getFriends(id: UUID):List<Member>
    * */
    @Test
    fun `test getFriends()`(){
        val member = Member("inu", "encrypted")
        val friend = Member("friend1", "encrypted1")
        val friend2 = Member("friend2", "encrypted2")
        entityManager.persist(member)
        entityManager.persist(friend)
        entityManager.persist(friend2)
        val friendship = Friendship(member, friend)
        val friendship2 = Friendship(member, friend2)
        member.addFreind(friendship)
        member.addFreind(friendship2)
        entityManager.persist(friendship)
        entityManager.persist(friendship2)
        entityManager.flush()

        val res = friendshipRepository.getFriends(member.id)
        assertThat(res[0].id).isEqualTo(friend.id)
        assertThat(res[1].id).isEqualTo(friend2.id)

    }


}