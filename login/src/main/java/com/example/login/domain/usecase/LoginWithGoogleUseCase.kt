package com.example.login.domain.usecase

import com.example.login.data.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(idToken: String): Result<Unit>{
        return authRepository.loginWithGoogle(idToken)
    }
}