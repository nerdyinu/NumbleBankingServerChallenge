package com.example.numblebankingserverchallenge.exception

sealed class CustomException(val code:ErrorCode) :RuntimeException(code.message) {
    class AccountNotFoundException:CustomException(ErrorCode.ACCOUNT_NOT_FOUND)
    class NotLoggedInException:CustomException(ErrorCode.UNAUTHORIZED)
    class UserExistsException:CustomException(ErrorCode.ALREADY_EXISTS_USER)
    class UserNotFoundException:CustomException(ErrorCode.USER_NOT_FOUND)
    class BadRequestException:CustomException(ErrorCode.INVALID_PARAMETER)
}