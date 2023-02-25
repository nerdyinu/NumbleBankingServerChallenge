package com.example.numblebankingserverchallenge.mockapi

import org.springframework.stereotype.Component
import java.util.*

@Component
class NumbleAlarmService {
    fun notify(userId:UUID, message:String){
        Thread.sleep(500)
    }
}