package com.app.cardashcam.feature_auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.cardashcam.data.local.AppDatabase
import com.app.cardashcam.data.repository.AuthRepository
import com.app.cardashcam.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(AppDatabase.get(app).userDao())
    private val session = SessionManager(app)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                val ok = repo.login(email, pass)
                if (ok) {
                    session.saveLogin(email)
                    _loginSuccess.value = true
                } else {
                    _error.value = "Wrong email or password"
                }
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
            }
        }
    }

    fun register(name: String, email: String, pass: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                val ok = repo.register(name, email, pass)
                if (ok) {
                    onDone()
                } else {
                    _error.value = "Email already exists"
                }
            } catch (e: Exception) {
                _error.value = "Registration failed: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
