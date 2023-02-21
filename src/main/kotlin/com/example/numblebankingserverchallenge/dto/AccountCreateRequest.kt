package com.example.numblebankingserverchallenge.dto

import java.util.UUID

data class AccountCreateRequest(val name:String, val amount:AccountBalance) {
}