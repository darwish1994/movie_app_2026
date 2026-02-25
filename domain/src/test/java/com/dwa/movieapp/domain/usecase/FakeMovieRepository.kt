package com.dwa.movieapp.domain.usecase

import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.model.MovieDetail
import com.dwa.movieapp.domain.repository.MovieRepository

class FakeMovieRepository : MovieRepository {

    var popularMoviesResult: Result<List<Movie>> = Result.success(emptyList())
    var movieDetailResult: Result<MovieDetail> = Result.failure(RuntimeException("Not set"))

    var lastRequestedPage: Int? = null
    var lastRequestedMovieId: Int? = null

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        lastRequestedPage = page
        return popularMoviesResult
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        lastRequestedMovieId = movieId
        return movieDetailResult
    }
}
