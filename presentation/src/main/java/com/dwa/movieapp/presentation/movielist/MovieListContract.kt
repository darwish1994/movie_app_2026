package com.dwa.movieapp.presentation.movielist

import com.dwa.movieapp.domain.model.Movie

object MovieListContract {

    data class State(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isPaginating: Boolean = false,
        val error: String? = null,
        val currentPage: Int = 1,
        val hasMorePages: Boolean = true
    )

    sealed interface Intent {
        data object LoadMovies : Intent
        data object RefreshMovies : Intent
        data object LoadNextPage : Intent
        data class MovieClicked(val movieId: Int) : Intent
        data object RetryClicked : Intent
    }

    sealed interface Effect {
        data class NavigateToDetail(val movieId: Int) : Effect
    }
}
