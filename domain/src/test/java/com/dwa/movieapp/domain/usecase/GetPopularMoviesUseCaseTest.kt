package com.dwa.movieapp.domain.usecase

import com.dwa.movieapp.domain.model.Movie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPopularMoviesUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        repository = FakeMovieRepository()
        useCase = GetPopularMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns success with movie list`() = runTest {
        val movies = listOf(
            Movie(id = 1, title = "Movie 1", posterPath = "/path1.jpg", releaseDate = "2024-01-01", voteAverage = 7.5, overview = "Overview 1"),
            Movie(id = 2, title = "Movie 2", posterPath = "/path2.jpg", releaseDate = "2024-02-01", voteAverage = 8.0, overview = "Overview 2")
        )
        repository.popularMoviesResult = Result.success(movies)

        val result = useCase(page = 1)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Movie 1", result.getOrNull()?.first()?.title)
        assertEquals(1, repository.lastRequestedPage)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        repository.popularMoviesResult = Result.failure(RuntimeException("Network error"))

        val result = useCase(page = 1)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke passes correct page number to repository`() = runTest {
        repository.popularMoviesResult = Result.success(emptyList())

        useCase(page = 3)

        assertEquals(3, repository.lastRequestedPage)
    }
}
