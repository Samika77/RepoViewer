package com.example.repoviewer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.network.Repo
import com.example.repoviewer.data.repository.AppRepository
import com.example.repoviewer.data.storage.KeyValueStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val repository: AppRepository,
    private val storage: KeyValueStorage
) : ViewModel() {
    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State> = _state

    private val _action = MutableSharedFlow<Action>()
    val action = _action.asSharedFlow()

    init {
        loadRepositories()
    }

    fun loadRepositories() {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                val repos = repository.getRepositories()
                if (repos.isEmpty()) {
                    _state.value = State.Empty
                    return@launch
                }
                _state.value = State.Loaded(repos)
            } catch (_: IOException) {
                _state.value = State.Error.ConnectionError
            } catch (_: Exception) {
                _state.value = State.Error.GeneralError
            }
        }
    }

    fun retryLoad() {
        loadRepositories()
    }

    fun refreshList() {
        loadRepositories()
    }

    fun onRepositorySelected(repoId: String) {
        if (repoId.isBlank()) return
        viewModelScope.launch {
            _action.emit(Action.RouteToDetail(repoId))
        }
    }

    fun logout() {
        viewModelScope.launch {
            storage.clearAuthToken()
            _action.emit(Action.NavigateToSignIn)
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        sealed class Error : State {
            object ConnectionError : Error()
            object GeneralError : Error()
        }
        object Empty : State
    }

    sealed interface Action {
        data class RouteToDetail(val repoId: String) : Action
        object NavigateToSignIn : Action
    }
}