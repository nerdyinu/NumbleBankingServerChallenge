package com.example.numblebankingserverchallenge.dto

import java.io.Serializable

data class AccountCreateRequest(val name:String, val amount:AccountBalance):Serializable {
}