package com.dwa.movieapp.domain.usecase

import com.dwa.movieapp.domain.model.Genre
import com.dwa.movieapp.domain.model.MovieDetail
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieDetailUseCase

    @Before
    fun setup() {
        repository = FakeMovieRepository()
        useCase = GetMovieDetailUseCase(repository)
    }

    @Test
    fun `invoke returns success with movie detail`() = runTest {
        val movieDetail = MovieDetail(
            id = 1,
            title = "Movie 1",
            overview = "A great movie",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 1000,
            runtime = 120,
            genres = listOf(Genre(id = 28, name = "Action")),
            tagline = "Action-packed"
        )
        repository.movieDetailResult = Result.success(movieDetail)

        val result = useCase(movieId = 1)

        assertTrue(result.isSuccess)
        assertEquals("Movie 1", result.getOrNull()?.title)
        assertEquals(120, result.getOrNull()?.runtime)
        assertEquals(1, repository.lastRequestedMovieId)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        repository.movieDetailResult = Result.failure(RuntimeException("Not found"))

        val result = useCase(movieId = 999)

        assertTrue(result.isFailure)
        assertEquals("Not found", result.exceptionOrNull()?.message)
    }
}
