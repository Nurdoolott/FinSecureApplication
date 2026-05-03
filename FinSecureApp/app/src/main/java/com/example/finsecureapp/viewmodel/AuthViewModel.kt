package com.example.finsecureapp.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.*
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.utils.Resource
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    // Временное хранение данных между экранами
    var pendingFullName: String? = null,
    var pendingPassword: String? = null,
    var pendingEmail: String? = null,
    var pendingPhone: String? = null,
    var pendingNewPassword: String? = null
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState

    private val _otpSentState = MutableStateFlow<Resource<String>?>(null)
    val otpSentState: StateFlow<Resource<String>?> = _otpSentState

    private val _registerState = MutableStateFlow<Resource<StartRegisterResponse>?>(null)
    val registerState: StateFlow<Resource<StartRegisterResponse>?> = _registerState

    private val _forgotPasswordState = MutableStateFlow<Resource<ForgotPasswordResponse>?>(null)
    val forgotPasswordState: StateFlow<Resource<ForgotPasswordResponse>?> = _forgotPasswordState

    // Сохраняем verificationId после отправки OTP
    var verificationId: String? = null

    // Шаг 1 — отправить OTP
    fun sendOtp(phoneNumber: String, activity: Activity) {
        _otpSentState.value = Resource.Loading
        viewModelScope.launch {
            // Сначала проверяем номер на бэкенде
            val checkResult = repository.checkPhone(phoneNumber)
            if (checkResult is Resource.Error) {
                _otpSentState.value = Resource.Error(checkResult.message)
                return@launch
            }
            // Только потом отправляем OTP
            repository.sendOtp(phoneNumber, activity,
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {}
                    override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                        _otpSentState.value = Resource.Error(e.message ?: "OTP send failed")
                    }
                    override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verificationId = id
                        _otpSentState.value = Resource.Success("OTP sent")
                    }
                }
            )
        }
    }

    // Шаг 2 — подтвердить OTP и зарегистрировать
    fun verifyOtpAndRegister(
        otpCode: String,
        fullName: String,
        password: String,
        email: String?
    ) {
        val vId = verificationId ?: return
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val tokenResult = repository.verifyOtpAndGetToken(vId, otpCode)
            if (tokenResult is Resource.Success) {
                _registerState.value = repository.startRegister(
                    fullName, password, email, tokenResult.data
                )
            } else {
                _registerState.value = Resource.Error((tokenResult as Resource.Error).message)
            }
        }
    }

    // Шаг 2 — подтвердить OTP и сбросить пароль
    fun verifyOtpAndResetPassword(otpCode: String, newPassword: String) {
        val vId = verificationId ?: return
        viewModelScope.launch {
            _forgotPasswordState.value = Resource.Loading
            val tokenResult = repository.verifyOtpAndGetToken(vId, otpCode)
            if (tokenResult is Resource.Success) {
                _forgotPasswordState.value = repository.forgotPassword(
                    tokenResult.data, newPassword
                )
            } else {
                _forgotPasswordState.value = Resource.Error((tokenResult as Resource.Error).message)
            }
        }
    }

    fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = repository.login(phoneNumber, password)
            if (result is Resource.Success) {
                tokenManager.saveToken(result.data.token)
            }
            _loginState.value = result
        }
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}