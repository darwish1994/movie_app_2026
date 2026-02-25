package com.dwa.movieapp.data.mapper

import com.dwa.movieapp.data.remote.dto.GenreDto
import com.dwa.movieapp.data.remote.dto.MovieDetailDto
import com.dwa.movieapp.data.remote.dto.MovieDto
import com.dwa.movieapp.domain.model.Genre
import com.dwa.movieapp.domain.model.Movie
import com.dwa.movieapp.domain.model.MovieDetail

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        overview = overview
    )
}

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genres.map { it.toDomain() },
        tagline = tagline
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
