package com.example.repoviewer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val token = MutableLiveData<String>()
    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> = _actions.asSharedFlow()

    fun refreshState() {
        _state.value = _state.value
    }

    fun onSignButtonPressed() {
        val currentToken = token.value
        if (currentToken.isNullOrBlank()) {
            _state.value = State.InvalidInput(ErrorReason.EMPTY_TOKEN)
            return
        }

        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val userInfo = repository.signIn(currentToken)
                if (userInfo.tokenValid) {
                    _actions.emit(Action.RouteToMain)
                    _state.value = State.Idle
                }
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    _state.value = State.InvalidInput(ErrorReason.INVALID_TOKEN)
                    return@launch
                }
                val message = e.message.takeUnless { it.isNullOrBlank() }
                if (message != null) {
                    _actions.emit(Action.ShowError(message))
                } else {
                    _state.value = State.InvalidInput(ErrorReason.UNKNOWN_ERROR)
                }
                _state.value = State.Idle
            }
        }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reasonCode: ErrorReason) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

    enum class ErrorReason {
        EMPTY_TOKEN,
        INVALID_TOKEN,
        UNKNOWN_ERROR
    }
}