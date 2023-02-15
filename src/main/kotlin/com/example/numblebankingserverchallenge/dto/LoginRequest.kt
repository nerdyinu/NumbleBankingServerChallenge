package com.example.numblebankingserverchallenge.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest (@JsonProperty("username")val username:String,@JsonProperty("pw") val pw:String?=null)