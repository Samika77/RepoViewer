package com.example.repoviewer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.network.RepoDetails
import com.example.repoviewer.data.repository.AppRepository
import com.example.repoviewer.data.storage.KeyValueStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val repository: AppRepository,
    private val storage: KeyValueStorage
) : ViewModel() {
    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State> = _state

    private val _action = MutableSharedFlow<Action>()
    val action = _action.asSharedFlow()

    fun loadRepositoryDetails(repoId: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                val repoDetails = repository.getRepository(repoId)
                _state.value = State.Loaded(
                    githubRepo = repoDetails,
                    readmeState = ReadmeState.Loading
                )
                try {
                    val readmeMarkdown = repository.getRepositoryReadme(
                        ownerName = repoDetails.owner.login,
                        repositoryName = repoDetails.name,
                        branchName = repoDetails.defaultBranch
                    )
                    val readmeState = if (readmeMarkdown.isBlank()) {
                        ReadmeState.Empty
                    } else {
                        ReadmeState.Loaded(readmeMarkdown)
                    }
                    _state.value = State.Loaded(
                        githubRepo = repoDetails,
                        readmeState = readmeState
                    )
                } catch (_: Exception) {
                    _state.value = State.Loaded(
                        githubRepo = repoDetails,
                        readmeState = ReadmeState.Error
                    )
                }
            } catch (_: Exception) {
                _state.value = State.Error
            }
        }
    }

    fun retryLoad(repoId: String) {
        loadRepositoryDetails(repoId)
    }

    fun logout() {
        viewModelScope.launch {
            storage.clearAuthToken()
            _action.emit(Action.NavigateToSignIn)
        }
    }

    sealed interface State {
        object Loading : State

        object Error : State

        data class Loaded(
            val githubRepo: RepoDetails,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState

        object Error : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    sealed interface Action {
        object NavigateToSignIn : Action
    }
}