package com.example.numblebankingserverchallenge.dto

import com.example.numblebankingserverchallenge.domain.Account
import java.util.*

data class AccountDTO(val ownerId: UUID,val accountId:UUID,val name:String, val balance:Long){
    constructor(account:Account):this(account.owner.id,account.id, account.name, account.balance)
}
