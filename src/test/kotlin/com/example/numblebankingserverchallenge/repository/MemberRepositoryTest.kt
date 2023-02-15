package com.example.numblebankingserverchallenge.repository

import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.repository.member.MemberRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class MemberRepositoryTest @Autowired constructor(private val memberRepository: MemberRepository, @PersistenceContext private val entityManager: EntityManager){
//    fun getFriends(id: UUID): List<Member>
//    fun findByUsername(username:String): Member?
//    fun findByUsernameAndEncryptedPassword(username: String, encrypted:String): Member?
    @Test
    fun `should save Member Entity`(){
        val username = "Inu"
        val pw = "12345"
        val member =Member(username,pw).let{memberRepository.save(it)}
        assertThat(member.username).isEqualTo(username)
        assertThat(member.encryptedPassword).isEqualTo(pw)
    }
}