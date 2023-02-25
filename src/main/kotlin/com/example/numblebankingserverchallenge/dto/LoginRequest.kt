package com.example.numblebankingserverchallenge.dto

import java.io.Serializable

data class LoginRequest (val username:String,val pw:String?=null):Serializable