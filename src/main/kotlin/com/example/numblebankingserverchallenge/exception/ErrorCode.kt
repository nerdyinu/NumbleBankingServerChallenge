package com.example.numblebankingserverchallenge.exception

sealed class ErrorCode(val status:Int, val message:String) {
    object INVALID_PARAMETER:ErrorCode(400,"잘못된 요청입니다.")
    object UNAUTHORIZED:ErrorCode(401, "로그인이 필요합니다.")
    object ACCOUNT_NOT_FOUND:ErrorCode(404,"존재하지 않는 계좌입니다.")
    object USER_NOT_FOUND:ErrorCode(404, "존재하지 않는 유저입니다.")
    object ALREADY_EXISTS_USER:ErrorCode(409, "이미 존재하는 유저명입니다.")
}