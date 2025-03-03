package com.example.login.domain.usecase

import com.example.login.data.repository.AuthRepository
import com.google.firebase.Timestamp
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(nome: String, email: String, dataNascimento: Timestamp?): Result<Unit>{
        return authRepository.createUser(nome, email, dataNascimento)
    }
}