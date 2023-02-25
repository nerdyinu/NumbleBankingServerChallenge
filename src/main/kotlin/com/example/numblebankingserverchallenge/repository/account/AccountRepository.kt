package com.example.numblebankingserverchallenge.repository.account

import com.example.numblebankingserverchallenge.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository :JpaRepository<Account, UUID>, AccountRepositoryCustom{
}