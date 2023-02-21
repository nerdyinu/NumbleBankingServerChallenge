package com.example.numblebankingserverchallenge.dto

import jakarta.persistence.Column
import jakarta.persistence.Embeddable


@JvmInline
value class AccountBalance (val balance:Long){
}