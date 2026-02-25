package com.dwa.movieapp.presentation.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwa.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.dwa.movieapp.presentation.movielist.MovieListContract.Effect
import com.dwa.movieapp.presentation.movielist.MovieListContract.Intent
import com.dwa.movieapp.presentation.movielist.MovieListContract.State
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
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(Intent.LoadMovies)
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadMovies -> loadMovies()
            is Intent.RefreshMovies -> refreshMovies()
            is Intent.LoadNextPage -> loadNextPage()
            is Intent.MovieClicked -> navigateToDetail(intent.movieId)
            is Intent.RetryClicked -> loadMovies()
        }
    }

    private fun loadMovies() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPopularMoviesUseCase(page = 1)
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            movies = movies,
                            isLoading = false,
                            currentPage = 1,
                            hasMorePages = movies.isNotEmpty()
                        )
                    }
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

    private fun refreshMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            getPopularMoviesUseCase(page = 1)
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            movies = movies,
                            isRefreshing = false,
                            currentPage = 1,
                            hasMorePages = movies.isNotEmpty()
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = throwable.message ?: "An unexpected error occurred"
                        )
                    }
                }
        }
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isPaginating || !currentState.hasMorePages) return
        viewModelScope.launch {
            val nextPage = currentState.currentPage + 1
            _state.update { it.copy(isPaginating = true) }
            getPopularMoviesUseCase(page = nextPage)
                .onSuccess { movies ->
                    _state.update {
                        it.copy(
                            movies = it.movies + movies,
                            isPaginating = false,
                            currentPage = nextPage,
                            hasMorePages = movies.isNotEmpty()
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isPaginating = false) }
                }
        }
    }

    private fun navigateToDetail(movieId: Int) {
        viewModelScope.launch {
            _effect.send(Effect.NavigateToDetail(movieId))
        }
    }
}
