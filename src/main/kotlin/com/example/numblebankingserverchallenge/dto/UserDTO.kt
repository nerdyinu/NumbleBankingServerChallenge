package com.example.numblebankingserverchallenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.io.Serializable
import java.util.*

data class UserDTO @QueryProjection constructor (val id: UUID, val username:String):Serializable