package com.dwa.movieapp.domain.usecase

import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Movie>> {
        return repository.getPopularMovies(page)
    }
}
