package com.example.numblebankingserverchallenge.dto

data class LoginRequest (val username:String, @Transient val pw:String?=null)