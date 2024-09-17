package com.portfolio.auth.data

import android.util.Patterns
import com.portfolio.auth.domain.EmailValidator

class AndroidEmailValidator(): EmailValidator{
    override fun validate(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}