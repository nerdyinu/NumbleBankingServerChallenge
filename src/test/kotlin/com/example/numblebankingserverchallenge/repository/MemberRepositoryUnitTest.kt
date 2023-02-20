package com.example.numblebankingserverchallenge.repository


import com.example.numblebankingserverchallenge.JpaTestConfig
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import


@DataJpaTest
@Import(JpaTestConfig::class)
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