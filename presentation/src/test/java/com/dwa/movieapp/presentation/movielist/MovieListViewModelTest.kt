package com.dwa.movieapp.presentation.movielist

import app.cash.turbine.test
import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.usecase.GetPopularMoviesUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieRepository()
        getPopularMoviesUseCase = GetPopularMoviesUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MovieListViewModel {
        return MovieListViewModel(getPopularMoviesUseCase)
    }

    @Test
    fun `initial load fetches movies successfully`() = runTest {
        val movies = listOf(
            Movie(id = 1, title = "Movie 1", posterPath = null, releaseDate = "2024-01-01", voteAverage = 7.0, overview = "Overview")
        )
        fakeRepository.popularMoviesResults.add(Result.success(movies))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.movies.size)
        assertEquals("Movie 1", state.movies[0].title)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `initial load sets error state on failure`() = runTest {
        fakeRepository.popularMoviesResults.add(Result.failure(RuntimeException("Network error")))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `refresh replaces movies with fresh data`() = runTest {
        val initialMovies = listOf(
            Movie(id = 1, title = "Old Movie", posterPath = null, releaseDate = "2024-01-01", voteAverage = 7.0, overview = "Overview")
        )
        val refreshedMovies = listOf(
            Movie(id = 2, title = "New Movie", posterPath = null, releaseDate = "2024-02-01", voteAverage = 8.0, overview = "Overview")
        )
        fakeRepository.popularMoviesResults.add(Result.success(initialMovies))
        fakeRepository.popularMoviesResults.add(Result.success(refreshedMovies))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(MovieListContract.Intent.RefreshMovies)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.movies.size)
        assertEquals("New Movie", state.movies[0].title)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `load next page appends movies`() = runTest {
        val page1 = listOf(
            Movie(id = 1, title = "Movie 1", posterPath = null, releaseDate = "2024-01-01", voteAverage = 7.0, overview = "Overview")
        )
        val page2 = listOf(
            Movie(id = 2, title = "Movie 2", posterPath = null, releaseDate = "2024-02-01", voteAverage = 8.0, overview = "Overview")
        )
        fakeRepository.popularMoviesResults.add(Result.success(page1))
        fakeRepository.popularMoviesResults.add(Result.success(page2))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.handleIntent(MovieListContract.Intent.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.movies.size)
        assertEquals("Movie 1", state.movies[0].title)
        assertEquals("Movie 2", state.movies[1].title)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `movie clicked emits navigation effect`() = runTest {
        fakeRepository.popularMoviesResults.add(Result.success(emptyList()))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.handleIntent(MovieListContract.Intent.MovieClicked(42))
            val effect = awaitItem()
            assertTrue(effect is MovieListContract.Effect.NavigateToDetail)
            assertEquals(42, (effect as MovieListContract.Effect.NavigateToDetail).movieId)
        }
    }
}
