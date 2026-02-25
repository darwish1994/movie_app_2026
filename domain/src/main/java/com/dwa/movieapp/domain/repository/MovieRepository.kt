package com.dwa.movieapp.domain.repository

import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.model.MovieDetail

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): Result<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
}
