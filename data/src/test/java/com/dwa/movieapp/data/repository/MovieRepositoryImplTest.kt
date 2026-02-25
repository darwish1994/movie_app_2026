package com.dwa.movieapp.data.repository

import com.dwa.movieapp.data.remote.api.MovieApiService
import com.dwa.movieapp.data.remote.dto.GenreDto
import com.dwa.movieapp.data.remote.dto.MovieDetailDto
import com.dwa.movieapp.data.remote.dto.MovieDto
import com.dwa.movieapp.data.remote.dto.MovieResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MovieRepositoryImplTest {

    private lateinit var apiService: MovieApiService
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = MovieRepositoryImpl(apiService)
    }

    @Test
    fun `getPopularMovies returns mapped domain models on success`() = runTest {
        val dto = MovieResponseDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1, title = "Test Movie", overview = "Overview",
                    posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg",
                    releaseDate = "2024-01-01", voteAverage = 7.5,
                    voteCount = 100, popularity = 50.0, genreIds = listOf(28)
                )
            ),
            totalPages = 10,
            totalResults = 200
        )
        coEvery { apiService.getPopularMovies(page = 1) } returns dto

        val result = repository.getPopularMovies(page = 1)

        assertTrue(result.isSuccess)
        val movies = result.getOrNull()!!
        assertEquals(1, movies.size)
        assertEquals("Test Movie", movies[0].title)
        assertEquals("/poster.jpg", movies[0].posterPath)
    }

    @Test
    fun `getPopularMovies returns failure on network error`() = runTest {
        coEvery { apiService.getPopularMovies(page = 1) } throws IOException("No internet")

        val result = repository.getPopularMovies(page = 1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `getMovieDetail returns mapped domain model on success`() = runTest {
        val dto = MovieDetailDto(
            id = 1, title = "Test Movie", overview = "Overview",
            posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01", voteAverage = 8.0,
            voteCount = 500, runtime = 120,
            genres = listOf(GenreDto(id = 28, name = "Action")),
            tagline = "Tagline"
        )
        coEvery { apiService.getMovieDetail(movieId = 1) } returns dto

        val result = repository.getMovieDetail(movieId = 1)

        assertTrue(result.isSuccess)
        val detail = result.getOrNull()!!
        assertEquals("Test Movie", detail.title)
        assertEquals(120, detail.runtime)
        assertEquals(1, detail.genres.size)
        assertEquals("Action", detail.genres[0].name)
    }

    @Test
    fun `getMovieDetail returns failure on network error`() = runTest {
        coEvery { apiService.getMovieDetail(movieId = 1) } throws IOException("Timeout")

        val result = repository.getMovieDetail(movieId = 1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getPopularMovies calls api with correct page`() = runTest {
        coEvery { apiService.getPopularMovies(page = 5) } returns MovieResponseDto(
            page = 5, results = emptyList(), totalPages = 10, totalResults = 200
        )

        repository.getPopularMovies(page = 5)

        coVerify { apiService.getPopularMovies(page = 5) }
    }
}
