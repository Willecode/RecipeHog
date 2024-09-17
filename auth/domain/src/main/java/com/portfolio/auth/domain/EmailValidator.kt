package com.portfolio.auth.domain

interface EmailValidator {
    fun validate(email:String): Boolean
}