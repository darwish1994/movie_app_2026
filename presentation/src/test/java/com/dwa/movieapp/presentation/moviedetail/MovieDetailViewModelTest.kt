package com.dwa.movieapp.presentation.moviedetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.dwa.movieapp.domain.model.Genre
import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.model.MovieDetail
import com.dwa.movieapp.domain.repository.MovieRepository
import com.dwa.movieapp.domain.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieDetailRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieDetailRepository()
        getMovieDetailUseCase = GetMovieDetailUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(movieId: Int = 1): MovieDetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to movieId))
        return MovieDetailViewModel(getMovieDetailUseCase, savedStateHandle)
    }

    @Test
    fun `loads movie detail on initialization`() = runTest {
        val detail = MovieDetail(
            id = 1, title = "Test Movie", overview = "Great movie",
            posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 1000,
            runtime = 120, genres = listOf(Genre(id = 28, name = "Action")),
            tagline = "Tagline"
        )
        fakeRepository.movieDetailResults.add(Result.success(detail))

        val viewModel = createViewModel(movieId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.movieDetail)
        assertEquals("Test Movie", state.movieDetail?.title)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `sets error state on failure`() = runTest {
        fakeRepository.movieDetailResults.add(Result.failure(RuntimeException("Not found")))

        val viewModel = createViewModel(movieId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.movieDetail)
        assertFalse(state.isLoading)
        assertEquals("Not found", state.error)
    }

    @Test
    fun `retry reloads movie detail`() = runTest {
        val detail = MovieDetail(
            id = 1, title = "Movie", overview = "Overview",
            posterPath = null, backdropPath = null,
            releaseDate = "2024-01-01", voteAverage = 7.0, voteCount = 100,
            runtime = 90, genres = emptyList(), tagline = null
        )
        fakeRepository.movieDetailResults.add(Result.failure(RuntimeException("Error")))
        fakeRepository.movieDetailResults.add(Result.success(detail))

        val viewModel = createViewModel(movieId = 1)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.error != null)

        viewModel.handleIntent(MovieDetailContract.Intent.RetryClicked)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.movieDetail)
        assertNull(state.error)
    }

    @Test
    fun `back clicked emits navigate back effect`() = runTest {
        val detail = MovieDetail(
            id = 1, title = "Movie", overview = "Overview",
            posterPath = null, backdropPath = null,
            releaseDate = "2024-01-01", voteAverage = 7.0, voteCount = 100,
            runtime = 90, genres = emptyList(), tagline = null
        )
        fakeRepository.movieDetailResults.add(Result.success(detail))

        val viewModel = createViewModel(movieId = 1)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(MovieDetailContract.Intent.BackClicked)
            val effect = awaitItem()
            assertTrue(effect is MovieDetailContract.Effect.NavigateBack)
        }
    }
}

class FakeMovieDetailRepository : MovieRepository {
    var movieDetailResults: MutableList<Result<MovieDetail>> = mutableListOf()
    private var callIndex = 0

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        return Result.success(emptyList())
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        val result = if (callIndex < movieDetailResults.size) {
            movieDetailResults[callIndex]
        } else {
            movieDetailResults.lastOrNull() ?: Result.failure(RuntimeException("Not set"))
        }
        callIndex++
        return result
    }
}
