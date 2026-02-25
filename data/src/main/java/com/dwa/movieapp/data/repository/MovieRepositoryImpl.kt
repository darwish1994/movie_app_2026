package com.dwa.movieapp.data.repository

import com.dwa.movieapp.data.mapper.toDomain
import com.dwa.movieapp.data.remote.api.MovieApiService
import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.model.MovieDetail
import com.dwa.movieapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        return try {
            val response = apiService.getPopularMovies(page = page)
            Result.success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return try {
            val response = apiService.getMovieDetail(movieId = movieId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
