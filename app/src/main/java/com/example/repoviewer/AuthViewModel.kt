package com.example.repoviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
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

    fun onSignButtonPressed() {
        val currentToken = token.value
        if (currentToken.isNullOrBlank()) {
            _state.value = State.InvalidInput("Token must not be empty")
            return
        }

        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val userInfo = repository.signIn(currentToken)
                if (userInfo.tokenValid) {
                    _actions.emit(Action.RouteToMain)
                    _state.value = State.Idle
                } else {
                    _state.value = State.InvalidInput("Invalid token")
                }
            } catch (e: Exception) {
                _actions.emit(
                    Action.ShowError(
                        e.message ?: "Unknown error"
                    )
                )
                _state.value = State.Idle
            }
        }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }
}