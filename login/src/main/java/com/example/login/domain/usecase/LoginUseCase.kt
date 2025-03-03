package com.example.login.domain.usecase

import com.example.login.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.login(email, password)
    }
}