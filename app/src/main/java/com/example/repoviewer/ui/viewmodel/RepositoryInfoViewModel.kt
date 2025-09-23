package com.example.repoviewer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.repository.AppRepository
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.domain.model.RepoDetails
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
            val repoDetails = try {
                repository.getRepository(repoId)
            } catch (_: Exception) {
                _state.value = State.Error
                return@launch
            }
            _state.value = State.Loaded(
                githubRepo = repoDetails,
                readmeState = ReadmeState.Loading
            )

            val readmeMarkdown = try {
                repository.getRepositoryReadme(
                    ownerName = repoDetails.ownerLogin,
                    repositoryName = repoDetails.name,
                    branchName = repoDetails.defaultBranch
                )
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    _state.value = State.Loaded(
                        githubRepo = repoDetails,
                        readmeState = ReadmeState.Empty
                    )
                } else {
                    _state.value = State.Loaded(
                        githubRepo = repoDetails,
                        readmeState = ReadmeState.Error
                    )
                }
                return@launch
            } catch (_: Exception) {
                _state.value = State.Loaded(
                    githubRepo = repoDetails,
                    readmeState = ReadmeState.Error
                )
                return@launch
            }

            val readmeState = if (readmeMarkdown.content.isBlank()) {
                ReadmeState.Empty
            } else {
                ReadmeState.Loaded(readmeMarkdown.content)
            }
            _state.value = State.Loaded(
                githubRepo = repoDetails,
                readmeState = readmeState
            )
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