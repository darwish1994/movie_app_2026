package com.dwa.movieapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dwa.movieapp.presentation.moviedetail.MovieDetailScreen
import com.dwa.movieapp.presentation.moviedetail.MovieDetailViewModel
import com.dwa.movieapp.presentation.movielist.MovieListScreen
import com.dwa.movieapp.presentation.movielist.MovieListViewModel
import kotlinx.serialization.Serializable

@Serializable
data object MovieListRoute

@Serializable
data class MovieDetailRoute(val movieId: Int)

private const val TRANSITION_DURATION = 400

@Composable
fun MovieNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MovieListRoute,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        }
    ) {
        composable<MovieListRoute> {
            val viewModel: MovieListViewModel = hiltViewModel()
            MovieListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { movieId ->
                    navController.navigate(MovieDetailRoute(movieId))
                }
            )
        }

        composable<MovieDetailRoute> {
            val viewModel: MovieDetailViewModel = hiltViewModel()
            MovieDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
