package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.TypedQuery
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension



@DataJpaTest
class MemberRepositoryUnitTest @Autowired  constructor(
    private val memberRepository: MemberRepository,
    private val entityManager: TestEntityManager
) {

    @Test
    fun `test find by username`() {
        val member = Member("inu", "encrypted")
        entityManager.persist(member)
        entityManager.flush()

        val res = memberRepository.findByUsername(member.username)


        assertThat(member.username).isEqualTo(res?.username)
        assertThat(member.encryptedPassword).isEqualTo( res?.encryptedPassword)
    }
    @Test
    fun `test findByUsernameAndEncryptedPassword`(){
        val member = Member("inu", "encrypted")
        entityManager.persist(member)
        entityManager.flush()
        val findMember =memberRepository.findByUsernameAndEncryptedPassword(member.username, member.encryptedPassword)
        assertThat(member).isEqualTo(findMember)
        assertThat(member.username).isEqualTo(findMember?.username)
        assertThat(member.encryptedPassword).isEqualTo(findMember?.encryptedPassword)
    }
}