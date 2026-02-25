package com.dwa.movieapp.presentation.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.movieapp.domain.usecase.GetMovieDetailUseCase
import com.dwa.movieapp.presentation.moviedetail.MovieDetailContract.Effect
import com.dwa.movieapp.presentation.moviedetail.MovieDetailContract.Intent
import com.dwa.movieapp.presentation.moviedetail.MovieDetailContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(Intent.LoadMovieDetail(movieId))
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadMovieDetail -> loadMovieDetail(intent.movieId)
            is Intent.RetryClicked -> loadMovieDetail(movieId)
            is Intent.BackClicked -> navigateBack()
        }
    }

    private fun loadMovieDetail(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getMovieDetailUseCase(id)
                .onSuccess { detail ->
                    _state.update { it.copy(movieDetail = detail, isLoading = false) }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "An unexpected error occurred"
                        )
                    }
                }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(Effect.NavigateBack)
        }
    }
}
