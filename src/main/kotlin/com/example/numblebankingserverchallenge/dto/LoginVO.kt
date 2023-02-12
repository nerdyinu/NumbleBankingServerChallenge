package com.example.numblebankingserverchallenge.dto

data class LoginVO (val username:String, @Transient val pw:String?=null)