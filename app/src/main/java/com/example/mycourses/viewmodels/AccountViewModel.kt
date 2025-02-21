package com.example.mycourses.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import com.example.mycourses.model.states.AccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    var user by mutableStateOf(User())
        private set

    var editedUser by mutableStateOf(User())
        private set

    init {
        loadAccountData()
    }

    private fun loadAccountData() {
        viewModelScope.launch {
            try {
                user = userRepository.getCurrentUser() ?: User()
                user.let {
                    combine(
                        courseRepository.getEnrolledCourses(user.id),
                        courseRepository.getMyCourses(user.id)
                    ) { enrolledCourses, myCourses ->
                        AccountUiState.Success(
                            enrolledCourses = enrolledCourses,
                            myCourses = myCourses,
                            user = user
                        )
                    }.collect { uiState ->
                        _uiState.value = uiState
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AccountUiState.Error(e.message.toString())
                Log.e("AccountViewModel", "Erro ao carregar dados da conta", e)
            }
        }
    }

    fun initialize() {
        editedUser = user
    }

    fun updateName(name: String) {
        editedUser = editedUser.copy(name = name)
    }

    fun updateEmail(email: String) {
        editedUser = editedUser.copy(email = email)
    }

    fun updateAge(age: Date) {
        editedUser = editedUser.copy(age = age)
    }

    fun saveChanges(onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(editedUser)
                onSuccess(editedUser)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                imageUri?.let {
                    userRepository.updateUser(user, it)
                    onComplete(true)
                }?: onComplete(false)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _logoutEvent.emit(Unit)
        }
    }

    fun resetUserState() {
        //_logoutState.value = false

    }

}