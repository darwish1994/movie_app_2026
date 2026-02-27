package com.dwa.movieapp.presentation.movielist

import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.repository.MovieRepository
import com.dwa.movieapp.domain.usecase.GetPopularMoviesUseCase

class FakeMovieRepository : MovieRepository {
    var popularMoviesResults: MutableList<Result<List<Movie>>> = mutableListOf()
    var movieDetailResult: Result<com.dwa.movieapp.domain.model.MovieDetail> =
        Result.failure(RuntimeException("Not set"))

    private var callIndex = 0

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        val result = if (callIndex < popularMoviesResults.size) {
            popularMoviesResults[callIndex]
        } else {
            popularMoviesResults.lastOrNull() ?: Result.success(emptyList())
        }
        callIndex++
        return result
    }

    override suspend fun getMovieDetail(movieId: Int): Result<com.dwa.movieapp.domain.model.MovieDetail> {
        return movieDetailResult
    }
}
