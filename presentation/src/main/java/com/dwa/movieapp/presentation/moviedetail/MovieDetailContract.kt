package com.dwa.movieapp.presentation.moviedetail

import com.dwa.movieapp.domain.model.MovieDetail

object MovieDetailContract {

    data class State(
        val movieDetail: MovieDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        data class LoadMovieDetail(val movieId: Int) : Intent
        data object RetryClicked : Intent
        data object BackClicked : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}
